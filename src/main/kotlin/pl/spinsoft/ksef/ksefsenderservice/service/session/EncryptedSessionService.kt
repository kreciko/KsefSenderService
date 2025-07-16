package pl.spinsoft.ksef.ksefsenderservice.service.session

import org.openapitools.client.models.AuthorisationChallengeResponse
import org.springframework.stereotype.Service
import pl.gov.mf.ksef.schema.gtw.svc.online.auth.request._2021._10._01._0001.InitSessionTokenRequest
import pl.gov.mf.ksef.schema.gtw.svc.types._2021._10._01._0001.*
import pl.spinsoft.ksef.ksefsenderservice.configuration.Configuration
import pl.spinsoft.ksef.ksefsenderservice.configuration.DocumentFormCodeConfig
import pl.spinsoft.ksef.ksefsenderservice.configuration.EncryptionDataConfig
import pl.spinsoft.ksef.ksefsenderservice.rest.KsefRestClient
import pl.spinsoft.ksef.ksefsenderservice.service.EncryptionService

@Service("encryptedSessionService")
class EncryptedSessionService(
    config: Configuration,
    restClient: KsefRestClient,
    encryptionService: EncryptionService,
    documentFormCodeConfig: DocumentFormCodeConfig,
    private val encryptionDataConfig: EncryptionDataConfig
) : BasicSessionService(config, restClient, encryptionService, documentFormCodeConfig) {
    val sessionCryptoContext = encryptionService.initSessionCryptoContext()

    override fun initSession(): String? {
        val authChallengeResponse = getChallenge() ?: throw IllegalStateException("Challenge is null")
        val initSessionTokenRequest = this.prepareInitSessionTokenRequest(authChallengeResponse)
        val initSessionTokenFile = prepareInitSessionTokenDocument(initSessionTokenRequest)
        initSessionToken(initSessionTokenFile)

        return this.getSessionToken()
    }

    override fun prepareInitSessionTokenRequest(authorisationChallengeResponse: AuthorisationChallengeResponse): InitSessionTokenRequest {
        val basicRequest = super.prepareInitSessionTokenRequest(authorisationChallengeResponse)
        val context = basicRequest.context
        val encryption = EncryptionType().apply {
            encryptionKey = EncryptionKeyType().apply {
                encoding = encryptionDataConfig.encoding
                algorithm = encryptionDataConfig.secondaryEncryptionAlgo
                size = encryptionDataConfig.secondaryByteCount
                value = encryptionService.encodeWithBase64(
                    encryptionService.encryptWithPublicKey(sessionCryptoContext.secretKey.encoded)
                )
            }

            encryptionInitializationVector = EncryptionInitializationVectorType().apply {
                encoding = encryptionDataConfig.encoding
                bytes = encryptionDataConfig.ivByteCount
                value = encryptionService.encodeWithBase64(
                    sessionCryptoContext.iv
                )
            }

            encryptionAlgorithmKey = EncryptionAlgorithmKeyType().apply {
                algorithm = encryptionDataConfig.mainEncryptionAlgo
                mode = encryptionDataConfig.mainEncryptionMode
                padding = encryptionDataConfig.mainEncryptionPadding.paddingInitFileVersion
            }

            encryptionAlgorithmData = EncryptionAlgorithmDataType().apply {
                algorithm = encryptionDataConfig.secondaryEncryptionAlgo
                mode = encryptionDataConfig.secondaryEncryptionMode
                padding = encryptionDataConfig.secondaryEncryptionPadding.paddingInitFileVersion
            }
        }

        context.encryption = encryption

        return basicRequest
    }
}