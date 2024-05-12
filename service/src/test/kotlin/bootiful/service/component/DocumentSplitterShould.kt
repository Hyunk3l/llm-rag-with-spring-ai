package bootiful.service.component

import bootiful.service.DocumentSplitter
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import org.springframework.ai.document.Document
import org.springframework.core.io.ClassPathResource

class DocumentSplitterShould {
    @Test
    fun `split a pdf file in 12 parts`() {
        val pdfResource = ClassPathResource("pdfs/medicaid-test.pdf")

        val result = DocumentSplitter(pdfResource).split()

        result.shouldBeInstanceOf<List<Document>>()
        result shouldHaveSize 12
    }
}
