package bootiful.service.component

import bootiful.model.ConsolePrinter
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait.forListeningPort
import org.testcontainers.utility.DockerImageName

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["wiremock.port=7777", "spring.ai.openai.base-url=http://localhost:7777"],
)
class ChatBotAppShould {

    @MockBean
    private lateinit var printer: ConsolePrinter

    @Test
    fun `answer correctly a medicaid question of FAQs`() {
        doNothing().`when`(printer).print("this is a test")

        verify(printer).print("this is a test")
    }

    companion object {
        @JvmStatic
        val mockHttpServer: WireMockServer = startWiremock()

        @JvmStatic
        @DynamicPropertySource
        fun postgresProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresContainer::getUsername)
            registry.add("spring.datasource.password", postgresContainer::getPassword)
        }

        val postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("pgvector/pgvector:pg16"))
            .withDatabaseName("vector_store")
            .withUsername("postgres")
            .withPassword("postgres")
            .waitingFor(forListeningPort())
            .also{ it.start() }

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
