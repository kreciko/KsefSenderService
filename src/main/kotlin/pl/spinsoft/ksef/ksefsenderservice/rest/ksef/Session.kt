package pl.spinsoft.ksef.ksefsenderservice.rest.ksef

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import pl.spinsoft.ksef.ksefsenderservice.configuration.Configuration
import pl.spinsoft.ksef.ksefsenderservice.model.request.AuthorizationChallengeRequestBody
import pl.spinsoft.ksef.ksefsenderservice.model.request.ContextIdentifier
import pl.spinsoft.ksef.ksefsenderservice.model.response.ChallengeResponse
import pl.spinsoft.ksef.ksefsenderservice.rest.KsefRequestData
import pl.spinsoft.ksef.ksefsenderservice.rest.KsefRestClient

@Component
class Session(private val config: Configuration, private val restClient: KsefRestClient) {

    fun getChallengeTime() : String? {
        val authorizationChallengeUrl = config.api.url + "api/online/Session/AuthorisationChallenge"
        var requestData = KsefRequestData(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, authorizationChallengeUrl)
        val payloadObj = AuthorizationChallengeRequestBody(ContextIdentifier("onip","9472006724"))
        var response : ChallengeResponse? =
            restClient.postRequestSync(requestData, payloadObj, ChallengeResponse::class)
        return response?.challenge
    }
}