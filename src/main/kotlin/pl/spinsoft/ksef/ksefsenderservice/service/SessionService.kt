package pl.spinsoft.ksef.ksefsenderservice.service

import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import pl.gov.mf.ksef.schema.gtw.svc.online.auth.request._2021._10._01._0001.InitSessionTokenRequest
import pl.gov.mf.ksef.schema.gtw.svc.online.types._2021._10._01._0001.AuthorisationContextTokenType
import pl.gov.mf.ksef.schema.gtw.svc.types._2021._10._01._0001.DocumentTypeType
import pl.gov.mf.ksef.schema.gtw.svc.types._2021._10._01._0001.EncryptionKeyType
import pl.gov.mf.ksef.schema.gtw.svc.types._2021._10._01._0001.EncryptionType
import pl.gov.mf.ksef.schema.gtw.svc.types._2021._10._01._0001.FormCodeType
import pl.gov.mf.ksef.schema.gtw.svc.types._2021._10._01._0001.ServiceType
import pl.gov.mf.ksef.schema.gtw.svc.types._2021._10._01._0001.SubjectIdentifierByCompanyType
import pl.gov.mf.ksef.schema.gtw.svc.types._2021._10._01._0001.SubjectIdentifierByType
import pl.spinsoft.ksef.ksefsenderservice.configuration.Configuration
import pl.spinsoft.ksef.ksefsenderservice.model.context.SessionCryptoContext
import pl.spinsoft.ksef.ksefsenderservice.model.request.AuthorizationChallengeRequestBody
import pl.spinsoft.ksef.ksefsenderservice.model.request.ContextIdentifier
import pl.spinsoft.ksef.ksefsenderservice.model.response.ChallengeResponse
import pl.spinsoft.ksef.ksefsenderservice.rest.KsefRequestData
import pl.spinsoft.ksef.ksefsenderservice.rest.KsefRestClient
import pl.spinsoft.ksef.ksefsenderservice.shared.XmlWriter
import java.io.File
import java.time.Instant

@Service
class SessionService(
    private val config: Configuration,
    private val encryptionService: EncryptionService,
    private val restClient: KsefRestClient) {

    private var sessionCryptoContext : SessionCryptoContext? = null

    fun getChallenge() : ChallengeResponse? {
        val authorizationChallengeUrl = config.api.url + "api/online/Session/AuthorisationChallenge"
        var requestData =
            KsefRequestData(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, authorizationChallengeUrl)
        val payloadObj = AuthorizationChallengeRequestBody(ContextIdentifier("onip", "9472006724"))
        var response : ChallengeResponse? =
            restClient.postRequestSync(requestData, payloadObj, ChallengeResponse::class)
        return response
    }

    fun initSession(initSessionTokenFile : File) : Boolean {


        val initSessionUrl = config.api.url+"api/online/Session/InitToken"
        var requestData =
            KsefRequestData(MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON, initSessionUrl)
        var response = restClient.postRequestSyncOctetStream(requestData, initSessionTokenFile)
        return true
    }

    fun initSessionCryptoContext() : Boolean {
        val aesKey = encryptionService.generateSecretAES()
        val iv = encryptionService.generateIV()
        sessionCryptoContext = SessionCryptoContext(aesKey, iv)
        return true
    }

    fun prepareInitSessionTokenDocument() : File {
        val challengeResponse = getChallenge()
        val initSessionReq = prepareInitSessionTokenRequest()
        val xmlWriter = XmlWriter()
        val output = xmlWriter.writeXmlToFile(initSessionReq, "output.xml")
        return output
    }

    private fun prepareInitSessionTokenRequest() : InitSessionTokenRequest {
        val challengeResponse = getChallenge()
        val initSessionReq = InitSessionTokenRequest()
        val subjectIdentifier : SubjectIdentifierByType = SubjectIdentifierByCompanyType()
        val context = AuthorisationContextTokenType()


        subjectIdentifier.identifier = "9472006724"
        context.identifier = subjectIdentifier
        context.challenge = /*"20250619-CR-38319DB98E-AEA1DB6ACA-5E"*/challengeResponse?.challenge

        val isoTimestamp = /*"2025-06-19T13:50:05.160Z"*/challengeResponse!!.timestamp
        val instant = Instant.parse(isoTimestamp)
        val challengeTime = instant.toEpochMilli()

        val tokenExt = "B29966FE795CE6A932CFBD05E1F29FEC79F4E888CBC9D0CAD165523CDE4FAF60|$challengeTime"
        val tokenEncrypted = encryptionService.encryptWithPublicKey(tokenExt.toByteArray())
        val tokenEncryptedBase64 = encryptionService.encodeWithBase64(tokenEncrypted)
        context.token = tokenEncryptedBase64

        var documentType = DocumentTypeType()
        var formCodeType = FormCodeType()
        formCodeType.systemCode = "FA (2)"
        formCodeType.schemaVersion = "1-0E"
        formCodeType.targetNamespace = "http://crd.gov.pl/wzor/2023/06/29/12648/"
        formCodeType.value="FA"

        documentType.service = ServiceType.K_SE_F
        documentType.formCode = formCodeType
        context.documentType = documentType


        initSessionReq.context = context

        return initSessionReq
    }

    private fun prepareBatchInitSessionTokenRequest() : InitSessionTokenRequest {
        val challengeResponse = getChallenge()
        val initSessionReq = prepareInitSessionTokenRequest()

        val context = initSessionReq.context
        val encryption = EncryptionType()

        val aesKeyEncrypted = encryptionService.encryptWithPublicKey(sessionCryptoContext!!.secretKey!!.encoded)
        val aesKeyEncryptedBase64 = encryptionService.encodeWithBase64(aesKeyEncrypted)
        val encryptionKeyType = EncryptionKeyType()
        encryptionKeyType.encoding = "Base64"
        encryptionKeyType.algorithm = "AES"
        encryptionKeyType.size = 256
        encryptionKeyType.value = aesKeyEncryptedBase64
        encryption.encryptionKey = encryptionKeyType




        context.encryption = encryption

        context.token = tokenEncryptedBase64



        return initSessionReq
    }

}