package pl.spinsoft.ksef.ksefsenderservice.model.request

data class AuthorizationChallengeRequestBody (
    val contextIdentifier: ContextIdentifier
) : GenericRequestBody()

data class ContextIdentifier(
    val type: String,
    val identifier: String
)
