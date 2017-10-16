package contentbot.repo;

import com.fasterxml.jackson.databind.JsonNode;
import contentbot.Loggable;
import contentbot.dto.ContentSnippet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Repository
public class FrankRepo implements Loggable {

    private final RestTemplate restTemplate;
    private final ExecutorService executorService;


    FrankRepo(@Qualifier("frankRestTemplate") final RestTemplate restTemplate,
              final ExecutorService executorService) {
        this.restTemplate = restTemplate;
        this.executorService = executorService;
    }

    public Set<ContentSnippet> fetchContentSnippet(final Set<String> ids) {

        return ids.stream().map(this::getContentSnippet)
                .map(CompletableFuture::join)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    private CompletableFuture<Optional<ContentSnippet>> getContentSnippet(final String id) {
        return CompletableFuture.supplyAsync(() -> {
            final JsonNode responseJsonNode = restTemplate.getForObject("/content/{id}", JsonNode.class, id);
            JsonNode fields = responseJsonNode.get("content").get("fields");
            return Optional.of(buildSnippet(fields));
        }, executorService)
                .exceptionally(throwable -> {
                    logger().error("Failed to fetch content for {}", id, throwable);
                    return Optional.empty();
                });
    }

    private ContentSnippet buildSnippet(final JsonNode fields) {
        return new ContentSnippet(fields.get("topic").asText(), fields.get("intro").asText(), fields.get("qcuSummary").asText());
    }
}
