package pl.spinsoft.ksef.ksefsenderservice.service.invoice

import org.openapitools.client.models.*
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import pl.spinsoft.ksef.ksefsenderservice.configuration.Configuration
import pl.spinsoft.ksef.ksefsenderservice.rest.KsefRestClient
import pl.spinsoft.ksef.ksefsenderservice.rest.KsefRequestData

import pl.spinsoft.ksef.ksefsenderservice.service.EncryptionService
import pl.spinsoft.ksef.ksefsenderservice.service.session.EncryptedSessionService
import java.io.File
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset


@Service
class InteractiveInvoiceService(
    val config: Configuration,
    var sessionService: EncryptedSessionService,
    var encryptionService: EncryptionService,
    var restClient: KsefRestClient,
) {
    val sentInvoiceSet = HashSet<SendInvoiceResponse>()

    fun sendInvoice(file: File): Boolean {
        val sendInvoiceUrl = "${config.api.url}api/online/Invoice/Send"
        val sessionToken = sessionService.getSessionToken()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.add("SessionToken",sessionToken)


        // Przygotowanie danych requestu
        val requestData = KsefRequestData(
            sendInvoiceUrl,
            headers
        )
        restClient.dynamicRequestJson({it.put()},requestData, prepareInvoiceRequest(file), SendInvoiceResponse::class)

        return true
    }

    fun queryForInvoice(from : OffsetDateTime, to: OffsetDateTime ): String {
        // Pobranie SessionToken
        val queryInvoiceUrl = "${config.api.url}api/online/Query/Invoice/Sync"
        val sessionToken = sessionService.getSessionToken()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.add("SessionToken",sessionToken)



        // Przygotowanie danych requestu
        val requestData = KsefRequestData(
            url = queryInvoiceUrl, // Zamień na właściwy URL endpointu dla zapytań
            headers = headers
        )



        val localDateFrom = LocalDate.of(2025, 7, 15).atStartOfDay().atOffset(ZoneOffset.UTC)
        val localDateTo = LocalDate.of(2025, 7, 16).atStartOfDay().atOffset(ZoneOffset.UTC)


        restClient.dynamicRequestJson({it.put()},requestData, prepareQueryInvoiceRequest(V2QueryCriteriaInvoiceType.SubjectType.subject1,
            localDateFrom,localDateTo), SendInvoiceResponse::class)
        // Wysłanie zapytania przez KsefRestClient
        return ""//restClient.query(requestData)
    }

    fun prepareInvoiceRequest(file: File): SendInvoiceRequest {
        val data = file.readBytes()
        val hashBase64 = encryptionService.encodeWithBase64(encryptionService.sha256(data))
        val size = data.size

        val encryptedBytes = encryptionService.encryptWithAES(data, sessionService.sessionCryptoContext)
        val encryptedHashBase64 = encryptionService.encodeWithBase64(encryptionService.sha256(encryptedBytes))
        val encryptedDataSize = encryptedBytes.size
        val encryptedDataString = encryptionService.encodeWithBase64(encryptedBytes)


        var invoiceHash = File1MBHashType().apply {
            this.fileSize = size
            this.hashSHA.algorithm = "SHA-256"
            this.hashSHA.encoding = "Base64"
            this.hashSHA.value = hashBase64
        }

        var encryptedInvoiceHash = File2MBHashType().apply {
            this.fileSize = encryptedDataSize
            this.hashSHA.algorithm = "SHA-256"
            this.hashSHA.encoding = "Base64"
            this.hashSHA.value = encryptedHashBase64
        }

        var invoicePayload = InvoicePayloadEncryptedType(type="encrypted", encryptedInvoiceHash = encryptedInvoiceHash, encryptedInvoiceBody = encryptedDataString).apply {}

        val sendInvoiceRequest = SendInvoiceRequest(invoiceHash, invoicePayload)

        return sendInvoiceRequest
    }

    fun prepareQueryInvoiceRequest(type: V2QueryCriteriaInvoiceType.SubjectType, from : OffsetDateTime, to: OffsetDateTime): V2QueryInvoiceRequest? {
        var queryCriteriaInvoiceRequest = V2QueryCriteriaInvoiceRangeType(type, "",from, to).apply {}
        val queryInvoiceRequest = V2QueryInvoiceRequest(queryCriteriaInvoiceRequest)
        return queryInvoiceRequest
    }



}