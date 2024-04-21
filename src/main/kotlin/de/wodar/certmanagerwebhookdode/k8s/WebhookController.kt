package de.wodar.certmanagerwebhookdode.k8s

import de.wodar.certmanagerwebhookdode.dode.DOClient
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class WebhookController(
    val doClient: DOClient
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @PostMapping(
        path = ["/"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun webhook(challengeRequestDTO: ChallengeRequestDTO): Mono<ChallengeResponseDTO> {
        logger.trace("Received: {}", challengeRequestDTO)

        return doClient.createRecord(challengeRequestDTO.key).map {
            logger.trace("Internally produced: {}", it)

            val challengeResponseDTO = if (it.isLeft()) {
                ChallengeResponseDTO(uid = challengeRequestDTO.uid, success = false)
            } else {
                ChallengeResponseDTO(uid = challengeRequestDTO.uid, success = true)
            }

            logger.trace("Returning: {}", challengeResponseDTO)
            return@map challengeResponseDTO
        }
    }

}