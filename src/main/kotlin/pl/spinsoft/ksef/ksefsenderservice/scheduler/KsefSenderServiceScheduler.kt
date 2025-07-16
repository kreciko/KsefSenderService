package pl.spinsoft.ksef.ksefsenderservice.scheduler


import org.apache.logging.log4j.LogManager
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import pl.gov.mf.ksef.schema.gtw.svc.online.auth.request._2021._10._01._0001.InitSessionTokenRequest
import pl.spinsoft.ksef.ksefsenderservice.configuration.Configuration
import pl.spinsoft.ksef.ksefsenderservice.service.invoice.InteractiveInvoiceService
import pl.spinsoft.ksef.ksefsenderservice.service.session.SessionService
import java.io.File

@Component
class KsefSenderServiceScheduler(private val config : Configuration, @Qualifier("encryptedSessionService") private val session : SessionService, private val invoiceService: InteractiveInvoiceService) {

    private val logger = LogManager.getLogger(KsefSenderServiceScheduler::class.java)

    @Scheduled(cron = "\${spinsoft.ksef.application.cron}")
    fun performTask() {

        logger.info("Performing ksef sender")
        var sessionToken = session.initSession()

        val invoiceFiles = listXmlFilesInDirectory("D:\\spinSoft\\ProjectsMetadata\\ksef\\input")

        invoiceFiles.forEach { invoiceFile ->
            invoiceService.sendInvoice(invoiceFile)
        }

    }

    fun listXmlFilesInDirectory(path: String): List<File> {
        val dir = File(path)
        return if (dir.exists() && dir.isDirectory) {
            dir.listFiles()?.filter { it.isFile && it.extension.lowercase() == "xml"} ?: emptyList()
        } else {
            emptyList()
        }
    }
}