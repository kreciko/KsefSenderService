package pl.spinsoft.ksef.ksefsenderservice.model.context

import javax.crypto.SecretKey

class SessionCryptoContext (var secretKey: SecretKey? = null, var iv: ByteArray? = null) {
    fun isInitialized() : Boolean {
        return secretKey != null && iv != null && iv!!.size == 16
    }
}