package pl.spinsoft.ksef.ksefsenderservice.model.context

import javax.crypto.SecretKey

class SessionCryptoContext (var secretKey: SecretKey, var iv: ByteArray) {
    fun isInitialized() : Boolean {
        return iv.size == 16
    }
}