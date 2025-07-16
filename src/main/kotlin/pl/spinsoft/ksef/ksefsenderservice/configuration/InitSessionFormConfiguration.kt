package pl.spinsoft.ksef.ksefsenderservice.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.spinsoft.ksef.ksefsenderservice.model.context.EncryptionPaddingEnum
import java.security.PublicKey

@Configuration
class EncryptionConfig {

    @Bean
    fun encryptionDataConfig(): EncryptionDataConfig {
        return EncryptionDataConfig(
            mainEncryptionAlgo = "RSA",
            mainEncryptionMode = "ECB",
            mainEncryptionPadding = EncryptionPaddingEnum.PKCS1Padding,
            secondaryEncryptionAlgo = "AES",
            secondaryEncryptionMode = "CBC",
            secondaryEncryptionPadding = EncryptionPaddingEnum.PKCS5Padding,
            secondaryByteCount = 256,
            encoding = "Base64",
            ivByteCount = 16
        )
    }
}

@Configuration
class DocumentFormCodeConfigProvider {

    @Bean
    fun documentFormCodeConfig(): DocumentFormCodeConfig {
        return DocumentFormCodeConfig(
            systemCode = "FA (2)",
            schemaVersion = "1-0E",
            targetNamespace = "http://crd.gov.pl/wzor/2023/06/29/12648/"
        )
    }
}

data class EncryptionDataConfig(
    val mainEncryptionAlgo: String,
    val mainEncryptionMode: String,
    val mainEncryptionPadding: EncryptionPaddingEnum,
    val secondaryEncryptionAlgo: String,
    val secondaryEncryptionMode: String,
    val secondaryEncryptionPadding: EncryptionPaddingEnum,
    val secondaryByteCount: Int,
    val encoding: String,
    val ivByteCount: Int
)

data class DocumentFormCodeConfig(
    val systemCode: String,
    val schemaVersion: String,
    val targetNamespace: String
)