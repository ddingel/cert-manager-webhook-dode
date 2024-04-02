package de.wodar.certmanagerwebhookdode

import de.wodar.certmanagerwebhookdode.dode.DomainOffensiveProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties(DomainOffensiveProperties::class)
@SpringBootApplication
class CertManagerWebhookDodeApplication

fun main(args: Array<String>) {
    runApplication<CertManagerWebhookDodeApplication>(*args)
}
