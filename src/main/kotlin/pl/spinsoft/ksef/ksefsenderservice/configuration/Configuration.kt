package pl.spinsoft.ksef.ksefsenderservice.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

@Configuration
@ConfigurationProperties(prefix = "spinsoft.ksef.application")
class Configuration {
    var hotfolder: String = ""
    var api: Api = Api()

    class Api {
        var url: String = ""
        var key: String = ""
    }

    @Bean
    fun publicKey(): PublicKey {
        var pem = String(Files.readAllBytes(Paths.get("src","main","resources","static","key","publicKey.pem")), charset("UTF-8"))


        // Usuń nagłówki/stopki PEM
        pem = pem.replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s+".toRegex(), "")

        val decoded = Base64.getDecoder().decode(pem)

        val keySpec = X509EncodedKeySpec(decoded)
        val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)

    }


}