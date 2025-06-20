package pl.spinsoft.ksef.ksefsenderservice

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pl.spinsoft.ksef.ksefsenderservice.service.EncryptionService
import java.security.PrivateKey
import java.util.Base64
import javax.crypto.Cipher

@SpringBootTest
class KsefSenderServiceApplicationTests {

    @Autowired
    val encryptionService: EncryptionService? = null

    @Test
    fun contextLoads() {
        val exampleToken = "QjI5OTY2RkU3OTVDRTZBOTMyQ0ZCRDA1RTFGMjlGRUM3OUY0RTg4OENCQzlEMENBRDE2NTUyM0NERTRGQUY2MHwxNzQ5MjMzODAy"

        //decryptWithPrivateKey()
    }

    fun decryptWithPrivateKey(encryptedBase64: String, privateKey: PrivateKey): String {
        val encryptedBytes = Base64.getDecoder().decode(encryptedBase64)
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }

}
