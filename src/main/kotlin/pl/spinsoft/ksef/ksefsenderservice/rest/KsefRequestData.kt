package pl.spinsoft.ksef.ksefsenderservice.rest

import org.springframework.http.MediaType

open class KsefRequestData (
    val contentType: MediaType,
    val accept: MediaType,
    val url: String


)
