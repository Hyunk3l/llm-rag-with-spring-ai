package bootiful.service

import bootiful.model.ConsolePrinter
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["wiremock.port=7777", "spring.ai.openai.base-url=http://localhost:7777"],
)
class ChatApplicationComponentTest {

    @MockkBean
    private lateinit var printer: ConsolePrinter

    @Test
    fun `should be able to ask a question to a LLM`() {
        val expectedResponse = "this is a test"
        every { printer.print(expectedResponse) } just Runs

        verify { printer.print(expectedResponse) }
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
