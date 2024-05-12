package bootiful.service;


import bootiful.model.Chatbot;
import bootiful.model.ConsolePrinter;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication(scanBasePackages = {"bootiful"})
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    static void init(VectorStore vectorStore, JdbcTemplate template, Resource pdfResource)
            throws Exception {

        template.update("delete from vector_store");

        var config = PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder().withNumberOfBottomTextLinesToDelete(3)
                        .withNumberOfTopPagesToSkipBeforeDelete(1)
                        .build())
                .withPagesPerDocument(1)
                .build();

        var pdfReader = new PagePdfDocumentReader(pdfResource, config);
        var textSplitter = new TokenTextSplitter();
        vectorStore.accept(textSplitter.apply(pdfReader.get()));

    }

    @Bean
    ApplicationRunner applicationRunner(
            Chatbot chatbot,
            VectorStore vectorStore,
            JdbcTemplate jdbcTemplate,
            @Value("file://${HOME}/code/llm-rag-with-spring-ai/service/medicaid-wa-faqs.pdf") Resource resource,
            ConsolePrinter printer
    ) {
        return args -> {
            init(vectorStore, jdbcTemplate, resource);
            var response = chatbot.chat("what should I know about the transition to consumer direct care network washington?");
            printer.print(response);
        };
    }
}
