package de.wodar.certmanagerwebhookdode.dode

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class DOClient(
    private val domainOffensiveProperties: DomainOffensiveProperties,
    private val objectMapper: ObjectMapper,
    webClientBuilder: WebClient.Builder
) {
    // Domain Offensive sends TEXT_HTML for json encoded body
    private val jsonForWronglyAnnouncedMediaType: ExchangeStrategies = ExchangeStrategies
        .builder()
        .codecs {
            it
                .customCodecs()
                .register(Jackson2JsonDecoder(jacksonObjectMapper(), MediaType.TEXT_HTML))
        }.build()

    private val webclient = webClientBuilder
        .baseUrl(domainOffensiveProperties.api.toString())
        .exchangeStrategies(jsonForWronglyAnnouncedMediaType)
        .build()

    fun createRecord(challenge: String): Mono<Either<DOError, DOSuccess>> {
        return webclient
            .get()
            .uri {
                it
                    .queryParam("token", domainOffensiveProperties.token)
                    .queryParam("domain", domainOffensiveProperties.domain)
                    .queryParam("value", challenge)
                    .build()
            }
            .retrieve()
            .bodyToMono(JsonNode::class.java)
            .map {
                val errorNode = it.get("error")
                return@map if (errorNode != null) {
                    DOError(errorNode.asText()).left()
                } else {
                    objectMapper.treeToValue(it, DOSuccess::class.java).right()
                }
            }
            .onErrorResume {
                (DOError("request for ${domainOffensiveProperties.domain} failed").left() as Either<DOError, DOSuccess>).toMono()
            }
    }
}

data class DOSuccess(
    val domain: String,
    val success: Boolean
)

data class DOError(
    val error: String
)