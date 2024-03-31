package bootiful.service

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.DefaultAsserter.assertEquals

@SpringBootTest
class ChatApplicationComponentTest {

    @Test
    fun `should ask a query to chatgpt using RAG`() {
        val capturedOutput = outputStream.toString()
        assertEquals("Don't match", """""
            {response=Under the new employment model, Consumer Direct Care Network Washington (CDWA) has replaced the Department of Social and Health Services (DSHS) as the new administrative employer for Individual Providers (IPs) in Washington. CDWA is now providing employment support for IPs, while consumers will continue as managing employers. This transition allows DSHS to focus on consumer case management. Consumers should work with their case managers to create a Plan of Care and access needed services. Once a consumer has chosen an IP to work with, they should complete the CDWA hiring requirements. It's important not to schedule an IP to work or start work until CDWA has provided an Okay to Work notification. Consumers can refer to the Managing Employer Handbook for more details on CDWA’s Consumer Resources. All IPs need to complete the CDWA hiring requirements, and consumers need to have an Okay to Work from CDWA before care can start. For more information, IPs can refer to the IP Employment Handbook on CDWA’s IP Resources.}
        """.trimIndent(), capturedOutput)
    }

    companion object {

        private lateinit var outputStream: ByteArrayOutputStream
        @JvmStatic
        @BeforeAll
        fun setUp(): Unit {
            outputStream = ByteArrayOutputStream()
            val printStream = PrintStream(outputStream)
            System.setOut(printStream)
        }
    }
}