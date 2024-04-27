package bootiful.service

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["wiremock.port=7777", "spring.ai.openai.base-url=http://localhost:7777"]
)
class ChatApplicationComponentTest {

    @Test
    fun `should be able to ask a question to a LLM`() {
        // Seam to test.
    }

    companion object {
        @JvmStatic
        val mockHttpServer: WireMockServer = startWiremock()

        private fun startWiremock(): WireMockServer {
            val wireMockServer = WireMockServer(7777)
            wireMockServer.start()
            WireMock.configureFor("localhost", 7777)
            val response = this::class.java.getResource("/wiremock/openai-embeddings-response.json")?.readText()
            WireMock.stubFor(
                    WireMock.post(WireMock.urlEqualTo("/v1/embeddings"))
                            .willReturn(
                                    WireMock.aResponse()
                                            .withStatus(200)
                                            .withHeader("Content-Type", "application/json")
                                            .withBody(response)
                            )
            )
            val completionResponse = this::class.java.getResource("/wiremock/openai-completion-response.json")
                    ?.readText()

            WireMock.stubFor(
                    WireMock.post(WireMock.urlEqualTo("/v1/chat/completions")).willReturn(
                            WireMock.aResponse()
                                    .withStatus(200)
                                    .withHeader("Content-Type", "application/json")
                                    .withBody(completionResponse)
                    )
            )
            return wireMockServer
        }
    }
}