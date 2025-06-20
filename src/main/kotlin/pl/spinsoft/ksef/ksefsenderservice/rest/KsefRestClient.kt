package pl.spinsoft.ksef.ksefsenderservice.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import pl.spinsoft.ksef.ksefsenderservice.model.request.GenericRequestBody
import pl.spinsoft.ksef.ksefsenderservice.model.response.GenericResponseBody
import java.io.File
import kotlin.reflect.KClass

@Component()
class KsefRestClient{

    val client: RestClient = RestClient.builder().build()
    val mapper: ObjectMapper = jacksonObjectMapper()


    fun <T : GenericRequestBody, Z : GenericResponseBody>
            postRequestSync(requestMetaData: KsefRequestData, payloadObj : T, responseBodyClass: KClass<Z>) : Z? {
        var result: Z? = null
        var bodyJson: String = mapper.writeValueAsString(payloadObj)
        client
            .post()
            .uri(requestMetaData.url)
            .contentType(requestMetaData.contentType)
            .accept(requestMetaData.accept)
            .body(bodyJson)
            .exchange { request, response ->
                val status: HttpStatusCode = response.statusCode
                if (status.is2xxSuccessful) {
                    println("POST succeeded: ${status}")
                    val bodyStr = response.body?.readAllBytes()?.decodeToString()
                    //result = response.body
                    result = mapper.readValue(bodyStr, responseBodyClass.java)
                    println("Response body: $bodyStr")

                } else {
                    println("POST failed with status: ${status}")
                    throw RuntimeException("POST failed with status: $status")
                }
            }

        return result
    }

    fun
            postRequestSyncOctetStream(requestMetaData: KsefRequestData, file : File/*, responseBodyClass: KClass<Z>*/) : ByteArray? {
        var result: ByteArray? = null
        //var bodyJson: String = mapper.writeValueAsString(payloadObj)
        client
            .post()
            .uri(requestMetaData.url)
            .contentType(requestMetaData.contentType)
            .accept(requestMetaData.accept)
            .body(file.readBytes())
            .exchange { request, response ->
                val status: HttpStatusCode = response.statusCode
                if (status.is2xxSuccessful) {
                    println("POST succeeded: ${status}")
                    val bodyStr = response.body?.readAllBytes()?.decodeToString()
                    //result = response.body
                    //result = mapper.readValue(bodyStr, responseBodyClass.java)
                    println("Response body: $bodyStr")

                } else {
                    println("POST failed with status: ${status}")
                    throw RuntimeException("POST failed with status: $status")
                }
            }

        return result
    }
}