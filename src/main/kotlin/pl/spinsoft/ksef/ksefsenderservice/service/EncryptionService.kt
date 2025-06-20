package pl.spinsoft.ksef.ksefsenderservice.service

import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

@Service
class EncryptionService(private val publicKey : PublicKey) {




    fun encryptWithPublicKey(data : ByteArray) : ByteArray {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(data)
    }

    fun encodeWithBase64(data : ByteArray) : String {
        return Base64.getEncoder().encodeToString(data)
    }

    fun generateSecretAES(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        return keyGen.generateKey()
    }

    fun generateIV(): ByteArray {
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)
        return iv
    }

}