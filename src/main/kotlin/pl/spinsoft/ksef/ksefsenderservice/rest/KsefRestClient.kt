package pl.spinsoft.ksef.ksefsenderservice.rest

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

import java.io.File
import kotlin.reflect.KClass



@Component()
final class KsefRestClient{

    val client: RestClient = RestClient.builder().build()
    val mapper: ObjectMapper = ObjectMapper().findAndRegisterModules()


    inline fun <reified T,reified Z : Any>
            dynamicRequestJson(requestMethod: (RestClient) -> RestClient.RequestBodyUriSpec, requestMetaData: KsefRequestData, payloadObj : T, responseBodyClass: KClass<Z>) : Z? {
        var result: Z? = null
        var bodyJson: String = mapper.writeValueAsString(payloadObj)
            result = requestMethod(client)
            .uri(requestMetaData.url)
            .headers {
                headers ->
                requestMetaData.headers.forEach {
                    key, values -> values.forEach { value -> headers.add(key, value) }
                }
             }
            .body(bodyJson)
            .retrieveAndMap(responseBodyClass, "${T::class.java} POST request failed")

        return result
    }

    fun<Z : Any>
            postRequestOctetStream(requestMetaData: KsefRequestData, file : File, responseBodyClass: KClass<Z>) : Z? {
        var result: Z? = null

        //var bodyJson: String = mapper.writeValueAsString(payloadObj)
        result = client
            .post()
            .uri(requestMetaData.url)
            .headers {
                headers ->
                requestMetaData.headers.forEach {
                        key, values -> values.forEach { value -> headers.add(key, value) }
                }
            }
            .body(file.readBytes())
            .retrieveAndMap(responseBodyClass, "OCTET-STREAM POST request failed")

        return result

    }

    // Wariant ze zwróceniem surowego tekstu
    fun <T>
            dynamicRequestJson(requestMetaData: KsefRequestData, payloadObj: T): String? {
        val bodyJson: String = mapper.writeValueAsString(payloadObj)

        return client
            .post()
            .uri(requestMetaData.url)
            .headers {
                headers ->
                requestMetaData.headers.forEach {
                        key, values -> values.forEach { value -> headers.add(key, value) }
                }
            }
            .body(bodyJson)
            .retrieve()
            .body(String::class.java)

    }

    // Wariant ze zwróceniem surowego tekstu
    fun postRequestOctetStream(requestMetaData: KsefRequestData, file : File): String? {
        return client
            .post()
            .uri(requestMetaData.url)
            .headers {
                headers ->
                requestMetaData.headers.forEach {
                        key, values -> values.forEach { value -> headers.add(key, value) }
                }
            }
            .body(file.readBytes())
            .retrieve()
            .body(String::class.java)

    }



    // Rozszerzenie dla ułatwienia obsługi mapowania odpowiedzi
    fun <Z : Any> RestClient.RequestBodySpec.retrieveAndMap(
        responseBodyClass: KClass<Z>,
        failureMessage: String
    ): Z? {
        return this.exchange { _, response ->
            val status = response.statusCode
            if (status.is2xxSuccessful) {
                val bodyStr = response.body?.readAllBytes()?.decodeToString()
                mapper.readValue(bodyStr, responseBodyClass.java)
            } else {
                throw RuntimeException("$failureMessage: $status")
            }
        }
    }

}