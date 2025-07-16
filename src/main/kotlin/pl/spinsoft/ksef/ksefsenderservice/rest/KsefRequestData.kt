package pl.spinsoft.ksef.ksefsenderservice.rest

import org.springframework.http.HttpHeaders


open class KsefRequestData (
    val url: String,
    val headers: HttpHeaders
)
