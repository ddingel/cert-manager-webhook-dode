package de.wodar.certmanagerwebhookdode.k8s

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthzController {

    @GetMapping(path = ["/healthz"])
    fun getHealthz(): ResponseEntity<Void> {
        return ResponseEntity<Void>(HttpStatus.OK)
    }
}