package bootiful.configuration;

import bootiful.model.ConsolePrinter;
import bootiful.service.DocumentSplitter;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class ApplicationConfiguration {

    @Bean
    DocumentSplitter documentSplitter(@Value("${application.medicaid.file}") Resource resource) {
        return new DocumentSplitter(resource);
    }

    @Bean
    VectorStore vectorStore(EmbeddingClient ec,
                            JdbcTemplate t) {
        return new PgVectorStore(t, ec);
    }

    @Bean
    TokenTextSplitter tokenTextSplitter() {
        return new TokenTextSplitter();
    }

    @Bean
    ConsolePrinter printer() {
        return new ConsolePrinter();
    }
}
