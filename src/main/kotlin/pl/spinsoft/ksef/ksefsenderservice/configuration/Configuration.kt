package pl.spinsoft.ksef.ksefsenderservice.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "spinsoft.ksef.application")
class Configuration {
    var hotfolder: String = ""
    var api: Api = Api()

    class Api {
        var url: String = ""
        var key: String = ""
    }
}