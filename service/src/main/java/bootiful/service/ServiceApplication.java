package bootiful.service;


import bootiful.model.Chatbot;
import bootiful.model.ConsolePrinter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication(scanBasePackages = {"bootiful"})
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    static void init(VectorStore vectorStore, JdbcTemplate template, DocumentSplitter documentSplitter)
            throws Exception {

        template.update("delete from vector_store");

        vectorStore.accept(documentSplitter.split());
    }

    @Bean
    ApplicationRunner applicationRunner(
            Chatbot chatbot,
            VectorStore vectorStore,
            JdbcTemplate jdbcTemplate,
            DocumentSplitter documentSplitter,
            ConsolePrinter printer
    ) {
        return args -> {
            init(vectorStore, jdbcTemplate, documentSplitter);
            var response = chatbot.chat("what should I know about the transition to consumer direct care network washington?");
            printer.print(response);
        };
    }
}
