package de.wodar.certmanagerwebhookdode.dode

import arrow.core.left
import arrow.core.right
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.Parameter.param
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
@SpringBootTest
class DOClientTest {

    companion object {
        @JvmStatic
        @Container
        val mockServer: MockServerContainer = MockServerContainer(
            DockerImageName.parse("mockserver/mockserver:mockserver-5.15.0")
        )

        @JvmStatic
        @DynamicPropertySource
        fun wireUpMockServer(registry: DynamicPropertyRegistry) {
            registry.add("dode.api") { "${mockServer.endpoint}/myapi" }
        }
    }

    @Autowired
    lateinit var doClient: DOClient

    @Test
    fun testCorrectTokenForDomainWithChallengeSucceeds() {
        // given
        MockServerClient(mockServer.host, mockServer.serverPort)
            .`when`(createValidRequestMatcher())
            .respond(createSuccessfulResponse())

        // when
        val response = doClient
            .createRecord("challenge")
            .blockOptional()
            .orElseThrow()

        // then
        assertThat(response.isRight()).isTrue()
        assertThat(response).isEqualTo(DOSuccess("example.com", true).right())
    }

    @Test
    fun testCorrectTokenForDomainWithEmptyChallengeFails() {
        // given
        MockServerClient(mockServer.host, mockServer.serverPort)
            .`when`(createEmptyValueRequestMatcher())
            .respond(createParameterMissingResponse())

        // when
        val response = doClient
            .createRecord("")
            .blockOptional()
            .orElseThrow()

        // then
        assertThat(response.isLeft()).isTrue()
        assertThat(response).isEqualTo(DOError("PARAMETER_MISSING").left())
    }

    private fun createValidRequestMatcher(): HttpRequest = request()
        .withMethod("GET")
        .withPath("/myapi")
        .withQueryStringParameters(
            param("token", "INVALID_TOKEN"),
            param("domain", "example.com"),
            param("value", "challenge")
        )

    private fun createEmptyValueRequestMatcher(): HttpRequest = request()
        .withMethod("GET")
        .withPath("/myapi")
        .withQueryStringParameters(
            param("token", "INVALID_TOKEN"),
            param("domain", "example.com"),
            param("value", "")
        )

    private fun createParameterMissingResponse(): HttpResponse =
        response()
            .withStatusCode(200)
            .withHeader("content-type", "text/html; charset=UTF-8")
            .withBody(
                """
                        {
                            "error": "PARAMETER_MISSING",
                            "errorCode": 9200,
                            "errorParam": "value"
                        }
                    """.trimIndent()
            )


    private fun createSuccessfulResponse(): HttpResponse =
        response()
            .withStatusCode(200)
            .withHeader("content-type", "text/html; charset=UTF-8")
            .withBody(
                """
                        {
                            "success": true,
                            "domain": "example.com"
                        }
                    """.trimIndent()
            )

}