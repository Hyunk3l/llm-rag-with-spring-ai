package bootiful.service

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["wiremock.port=7777", "spring.ai.openai.base-url=http://localhost:7777"]
)
class StaticTest {

    @Test
    fun `some`() {

    }

    companion object {
        @JvmStatic
        val mockHttpServer: WireMockServer = startWiremock()

        private fun startWiremock(): WireMockServer {
            val wireMockServer = WireMockServer(7777)
            wireMockServer.start()
            WireMock.configureFor("localhost", 7777)
            val response = this::class.java.getResource("wiremock/response.json")?.getContent().toString()
            WireMock.stubFor(
                    WireMock.post(WireMock.urlEqualTo("/v1/embeddings"))
                            .willReturn(
                                    WireMock.aResponse()
                                            .withStatus(200)
                                            .withHeader("Content-Type", "application/json")
                                            .withBody(response)
                            )
            )
            WireMock.stubFor(
                    WireMock.post(WireMock.urlEqualTo("/v1/chat/completions")).willReturn(
                            WireMock.aResponse()
                                    .withStatus(200)
                                    .withBody(response)
                    )
            )
            println("Running from static? ${wireMockServer.isRunning}")
            return wireMockServer
        }
    }
}