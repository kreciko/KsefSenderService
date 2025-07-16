package pl.spinsoft.ksef.ksefsenderservice.model.context

enum class EncryptionPaddingEnum (val paddingInitFileVersion : String,val paddingJavaVersion : String){
    PKCS5Padding("PKCS#7", "PKCS5Padding"),
    PKCS1Padding("PKCS#1", "PKCS1Padding"),
}