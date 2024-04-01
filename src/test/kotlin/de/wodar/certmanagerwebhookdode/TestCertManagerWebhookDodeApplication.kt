package de.wodar.certmanagerwebhookdode

import org.springframework.boot.fromApplication
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.with

@TestConfiguration(proxyBeanMethods = false)
class TestCertManagerWebhookDodeApplication

fun main(args: Array<String>) {
    fromApplication<CertManagerWebhookDodeApplication>().with(TestCertManagerWebhookDodeApplication::class).run(*args)
}
