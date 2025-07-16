package pl.spinsoft.ksef.ksefsenderservice.service.session


import org.openapitools.client.models.AuthorisationChallengeResponse
import org.openapitools.client.models.AuthorisationChallengeRequest
import org.openapitools.client.models.InitSessionResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import pl.gov.mf.ksef.schema.gtw.svc.online.auth.request._2021._10._01._0001.InitSessionTokenRequest
import pl.gov.mf.ksef.schema.gtw.svc.online.types._2021._10._01._0001.AuthorisationContextTokenType
import pl.gov.mf.ksef.schema.gtw.svc.types._2021._10._01._0001.DocumentTypeType
import pl.gov.mf.ksef.schema.gtw.svc.types._2021._10._01._0001.FormCodeType
import pl.gov.mf.ksef.schema.gtw.svc.types._2021._10._01._0001.ServiceType
import pl.gov.mf.ksef.schema.gtw.svc.types._2021._10._01._0001.SubjectIdentifierByCompanyType
import pl.spinsoft.ksef.ksefsenderservice.configuration.Configuration
import pl.spinsoft.ksef.ksefsenderservice.configuration.DocumentFormCodeConfig
import pl.spinsoft.ksef.ksefsenderservice.rest.KsefRequestData
import pl.spinsoft.ksef.ksefsenderservice.rest.KsefRestClient
import pl.spinsoft.ksef.ksefsenderservice.service.EncryptionService
import pl.spinsoft.ksef.ksefsenderservice.shared.XmlTools
import java.io.File
import java.util.UUID
import java.nio.file.Paths

@Service("basicSessionService")
class BasicSessionService(protected val config: Configuration,
                          protected val restClient: KsefRestClient,
                          protected val encryptionService: EncryptionService,
                          protected val documentFormCodeConfig: DocumentFormCodeConfig
) : SessionService  {

    private var sessionToken: String? = null

    override fun initSession(): String? {
        val authChallengeResponse = getChallenge() ?: throw IllegalStateException("Challenge is null")
        val initSessionTokenRequest = this.prepareInitSessionTokenRequest(authChallengeResponse)
        val initSessionTokenFile = prepareInitSessionTokenDocument(initSessionTokenRequest)
        initSessionToken(initSessionTokenFile)

        return this.sessionToken
    }

    override fun getSessionToken(): String? {
        return sessionToken
    }

    protected fun prepareInitSessionTokenRequest(authorisationChallengeResponse: AuthorisationChallengeResponse): InitSessionTokenRequest {
        val instant = authorisationChallengeResponse.timestamp.toInstant()
        val challengeTime = instant.toEpochMilli()

        val subjectIdentifier = SubjectIdentifierByCompanyType().apply {
            identifier = config.nip
        }

        val context = AuthorisationContextTokenType().apply {
            this.identifier = subjectIdentifier
            this.challenge = authorisationChallengeResponse.challenge
            val tokenExt = "${config.api.authToken}|$challengeTime"
            val tokenEncrypted = encryptionService.encryptWithPublicKey(tokenExt.toByteArray())
            val tokenEncryptedBase64 = encryptionService.encodeWithBase64(tokenEncrypted)
            this.token = tokenEncryptedBase64
            this.documentType = DocumentTypeType().apply {
                service = ServiceType.K_SE_F
                formCode = FormCodeType().apply {
                    systemCode = documentFormCodeConfig.systemCode
                    schemaVersion = documentFormCodeConfig.schemaVersion
                    targetNamespace = documentFormCodeConfig.targetNamespace
                    value = "FA"
                }
            }
        }

        return InitSessionTokenRequest().apply { this.context = context }
    }

    protected fun getChallenge(): AuthorisationChallengeResponse? {
        val authorizationChallengeUrl = "${config.api.url}api/online/Session/AuthorisationChallenge"
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_JSON

        val requestData = KsefRequestData(authorizationChallengeUrl, headers)
        val payload = AuthorisationChallengeRequest(org.openapitools.client.models.SubjectIdentifierByCompanyType("onip", config.nip))
        return restClient.dynamicRequestJson({it.post()}, requestData, payload, AuthorisationChallengeResponse::class)
    }

    protected fun initSessionToken(initSessionTokenFile: File): String? {
        sessionToken = null
        val initSessionUrl = "${config.api.url}api/online/Session/InitToken"
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_OCTET_STREAM
        headers.accept = listOf(MediaType.APPLICATION_JSON)

        val requestData = KsefRequestData(initSessionUrl, headers)
        val sessionResponse : InitSessionResponse? = restClient.postRequestOctetStream(requestData, initSessionTokenFile, InitSessionResponse::class)
        sessionToken = sessionResponse?.sessionToken?.token

        return sessionToken
    }

    protected fun prepareInitSessionTokenDocument(req : InitSessionTokenRequest): File {
        val xmlTools = XmlTools()
        val tempDir = System.getProperty("java.io.tmpdir")
        val tempFilePath = Paths.get(tempDir, "ksef-initsession-${UUID.randomUUID()}.xml")
        return xmlTools.writeObjectToXmlFile(req, tempFilePath.toString())
    }
}