package pl.spinsoft.ksef.ksefsenderservice.scheduler

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

import pl.spinsoft.ksef.ksefsenderservice.rest.ksef.Session

@Component
class KsefSenderServiceScheduler(private val session : Session) {

    @Scheduled(cron = "\${spinsoft.ksef.application.cron}")
    fun performTask() {
        var challengeTime = session.getChallengeTime()
        println(challengeTime)
    }
}