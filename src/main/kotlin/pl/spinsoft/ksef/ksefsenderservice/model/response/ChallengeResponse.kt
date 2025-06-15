package pl.spinsoft.ksef.ksefsenderservice.model.response

data class ChallengeResponse(
    val timestamp: String,
    val challenge: String
) : GenericResponseBody()
