package contentbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import contentbot.dto.ApiGatewayRequest;
import contentbot.dto.ApiGatewayResponse;
import contentbot.google.AIRequest;
import contentbot.google.Fulfillment;
import contentbot.google.GoogleAssistantResponseMessages;
import contentbot.google.GsonFactory;
import contentbot.repo.FrankRepo;
import contentbot.repo.PapyrusRepo;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class GoogleActionsHandler implements Loggable {

    private final ObjectMapper objectMapper;
    private final PapyrusRepo papyrusRepo;
    private final FrankRepo frankRepo;

    GoogleActionsHandler(final ObjectMapper objectMapper,
                         final PapyrusRepo papyrusRepo,
                         final FrankRepo frankRepo) {
        this.objectMapper = objectMapper;
        this.papyrusRepo = papyrusRepo;
        this.frankRepo = frankRepo;
    }

    ApiGatewayResponse handle(final ApiGatewayRequest apiGatewayRequest) throws IOException {
        final AIRequest aiRequest = objectMapper.readValue(apiGatewayRequest.getBody(), AIRequest.class);
        logger().info("Parsed requested: {}", aiRequest);
        //final String contentQuery = inputJsonNode.get("result").get("parameters").get("content").asText();
        final Fulfillment fulfillment = new Fulfillment();
        //fulfillment.setSpeech("hello");
        final GoogleAssistantResponseMessages.ResponseChatBubble chatBubble = new GoogleAssistantResponseMessages.ResponseChatBubble();
        //chatBubble.setCustomizeAudio(true);
        final GoogleAssistantResponseMessages.ResponseChatBubble.Item item = new GoogleAssistantResponseMessages.ResponseChatBubble.Item();
        item.setTextToSpeech("text to speech");
        chatBubble.setItems(Arrays.asList(item));
        fulfillment.setMessages(Arrays.asList(chatBubble));
        //return new ApiGatewayResponse("{\"speech\" : \"hello\", \"contextOut\":[],\"data\":{\"google\":{\"expectUserResponse\":false,\"isSsml\":false,\"noInputPrompts\":[]}}}");
        return new ApiGatewayResponse(GsonFactory.getDefaultFactory().getGson().toJson(fulfillment));
    }

    private String fetchContent(final String contentQuery) {
        return frankRepo.fetchContentSnippet(papyrusRepo.fetchIds(contentQuery)).stream().collect(Collectors.joining(""));
    }

}
