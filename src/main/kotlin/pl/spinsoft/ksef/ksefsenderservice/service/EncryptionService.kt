package pl.spinsoft.ksef.ksefsenderservice.service

import org.springframework.stereotype.Service
import pl.spinsoft.ksef.ksefsenderservice.configuration.EncryptionDataConfig
import pl.spinsoft.ksef.ksefsenderservice.model.context.SessionCryptoContext
import java.security.PublicKey
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import java.security.MessageDigest

@Service
class EncryptionService(private val publicKey : PublicKey, private val encryptionDataConfig: EncryptionDataConfig) {


    fun initSessionCryptoContext() : SessionCryptoContext {
        val aesKey = generateSecretAES()
        val iv = generateIV()
        return SessionCryptoContext(aesKey, iv)
    }

    fun encryptWithPublicKey(data : ByteArray) : ByteArray {
        val algorithm = encryptionDataConfig.mainEncryptionAlgo
        val mode = encryptionDataConfig.mainEncryptionMode
        val padding = encryptionDataConfig.mainEncryptionPadding
        val cipher = Cipher.getInstance("$algorithm/$mode/${padding.paddingJavaVersion}")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(data)
    }

    fun encryptWithAES(data: ByteArray, sessionCryptoContext : SessionCryptoContext): ByteArray {
        val algorithm = encryptionDataConfig.secondaryEncryptionAlgo
        val mode = encryptionDataConfig.secondaryEncryptionMode
        val padding = encryptionDataConfig.secondaryEncryptionPadding
        val cipher = Cipher.getInstance("$algorithm/$mode/${padding.paddingJavaVersion}")
        val ivSpec = IvParameterSpec(sessionCryptoContext.iv)
        cipher.init(Cipher.ENCRYPT_MODE, sessionCryptoContext.secretKey, ivSpec)
        return cipher.doFinal(data)
    }

    fun encodeWithBase64(data : ByteArray) : String {
        return Base64.getEncoder().encodeToString(data)
    }

    fun sha256(data : ByteArray) : ByteArray {
        return MessageDigest.getInstance("SHA-256").digest(data)
    }

    fun generateSecretAES(): SecretKey {
        val keyGen = KeyGenerator.getInstance(encryptionDataConfig.secondaryEncryptionAlgo)
        keyGen.init(encryptionDataConfig.secondaryByteCount)
        return keyGen.generateKey()
    }

    fun generateIV(): ByteArray {
        val iv = ByteArray(encryptionDataConfig.ivByteCount)
        SecureRandom().nextBytes(iv)
        return iv
    }



}