package de.wodar.certmanagerwebhookdode.dode

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URL

@ConfigurationProperties(prefix = "dode")
data class DomainOffensiveProperties(val api: URL, val token: String, val domain: String)