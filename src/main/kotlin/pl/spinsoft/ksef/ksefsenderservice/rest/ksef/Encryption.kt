package pl.spinsoft.ksef.ksefsenderservice.rest.ksef

import pl.gov.mf.ksef.schema.gtw.svc.online.auth.request._2021._10._01._0001.InitSessionTokenRequest
import pl.gov.mf.ksef.schema.gtw.svc.online.types._2021._10._01._0001.AuthorisationContextTokenType
import pl.gov.mf.ksef.schema.gtw.svc.online.types._2021._10._01._0001.AuthorisationContextType
import pl.gov.mf.ksef.schema.gtw.svc.types._2021._10._01._0001.SubjectIdentifierByCompanyType
import pl.gov.mf.ksef.schema.gtw.svc.types._2021._10._01._0001.SubjectIdentifierByType
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec



class Encryption {

    val publicKey = loadRSAPublicKeyFromPEM()


    fun encrypt(data: String): String {

        // 1. Wygeneruj AES key i IV
        val aesKey: SecretKey = KeyGenerator.getInstance("AES").generateKey()
        val iv: ByteArray = SecureRandom.getSeed(16)


// 2. Zaszyfruj dane (np. token)
        val aesCipher: Cipher = Cipher.getInstance("AES/CB9C/PKCS5Padding")
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, IvParameterSpec(iv))
        val encryptedData: ByteArray? = aesCipher.doFinal(null)


// 3. Załaduj klucz publiczny RSA z PEM
        val rsaPublicKey: PublicKey? = loadRSAPublicKeyFromPEM()


// 4. Zaszyfruj AES key kluczem RSA
        val rsaCipher: Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        rsaCipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey)
        val encryptedAesKey: ByteArray? = rsaCipher.doFinal(aesKey.getEncoded())


// 5. Zakoduj wszystko do Base64 i wstaw do XML
        val aesKeyBase64: String? = Base64.getEncoder().encodeToString(encryptedAesKey)
        val ivBase64: String? = Base64.getEncoder().encodeToString(iv)

        return data
    }

    private fun loadRSAPublicKeyFromPEM() : PublicKey? {
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


    fun encryptWithPublicKey(data : ByteArray) : ByteArray {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(data)
    }

    fun encodeWithBase64(data : ByteArray) : String {
        return Base64.getEncoder().encodeToString(data)
    }
}