package pl.spinsoft.ksef.ksefsenderservice.scheduler

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

import pl.spinsoft.ksef.ksefsenderservice.service.SessionService
import java.io.File
import java.io.FileReader

@Component
class KsefSenderServiceScheduler(private val session : SessionService) {

    @Scheduled(cron = "\${spinsoft.ksef.application.cron}")
    fun performTask() {
        val initSessionTokenFile = session.prepareInitSessionTokenDocument()
        var fileForSessionReq = session.initSession(initSessionTokenFile)

    }
}