package pl.spinsoft.ksef.ksefsenderservice.service.session

import pl.gov.mf.ksef.schema.gtw.svc.online.auth.request._2021._10._01._0001.InitSessionTokenRequest
import java.io.File

interface SessionService {
    //var sessionToken : String?
    fun initSession() : String?
    fun getSessionToken() : String?


}