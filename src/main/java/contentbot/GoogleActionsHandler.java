package contentbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import contentbot.dto.ApiGatewayRequest;
import contentbot.dto.ApiGatewayResponse;
import contentbot.dto.ContentSnippet;
import contentbot.google.Fulfillment;
import contentbot.google.GoogleAssistantResponseMessages;
import contentbot.google.GsonFactory;
import contentbot.repo.FrankRepo;
import contentbot.repo.PapyrusRepo;
import contentbot.repo.SessionNewstickerStepRepo;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class GoogleActionsHandler implements Loggable {

    private final ObjectMapper objectMapper;
    private final PapyrusRepo papyrusRepo;
    private final FrankRepo frankRepo;
    private final SessionNewstickerStepRepo sessionNewstickerStepRepo;

    GoogleActionsHandler(final ObjectMapper objectMapper,
                         final PapyrusRepo papyrusRepo,
                         final FrankRepo frankRepo,
                         final SessionNewstickerStepRepo sessionNewstickerStepRepo) {
        this.objectMapper = objectMapper;
        this.papyrusRepo = papyrusRepo;
        this.frankRepo = frankRepo;
        this.sessionNewstickerStepRepo = sessionNewstickerStepRepo;
    }

    ApiGatewayResponse handle(final ApiGatewayRequest apiGatewayRequest) throws IOException {
        final JsonNode inputJsonNode = objectMapper.readTree(apiGatewayRequest.getBody());
        final String sessionId = inputJsonNode.get("sessionId").asText();
        final Fulfillment fulfillment = new Fulfillment();
        //fulfillment.setSpeech("hello");
        final GoogleAssistantResponseMessages.ResponseChatBubble chatBubble = new GoogleAssistantResponseMessages.ResponseChatBubble();
        chatBubble.setCustomizeAudio(true);
        //item.setTextToSpeech("text to speech");
        final Set<ContentSnippet> snippets = fetchContent();
        final Optional<ContentSnippet> contentSnippetOptional = snippets.stream().filter(contentSnippet1 -> !sessionNewstickerStepRepo.getReadIds(sessionId).contains(contentSnippet1.getId())).findFirst();

        if (contentSnippetOptional.isPresent()) {
            final ContentSnippet contentSnippet = contentSnippetOptional.get();
            logger().info("Delivering snippet: {}", contentSnippet.getId());
            final GoogleAssistantResponseMessages.ResponseChatBubble.Item item = new GoogleAssistantResponseMessages.ResponseChatBubble.Item();
            item.setSsml("<speak xmlns=\"http://www.w3.org/2001/10/synthesis\"\n" +
                    "       xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n" +
                    "       version=\"1.0\">\n" +
                    "  <metadata>\n" +
                    "    <dc:title xml:lang=\"en\">Content qcu summary</dc:title>\n" +
                    "  </metadata>\n" +
                    "\n" +
                    String.format(
                            "  <p>\n" +
                                    "    <s xml:lang=\"de-DE\">\n" +
                                    "      <voice name=\"David\" gender=\"male\" age=\"25\">\n" +
                                    "        <emphasis>%s</emphasis> <break time=\"2s\" /> %s <break time=\"2s\" /> %s\n" +
                                    "      </voice>\n" +
                                    "    </s>\n" +
                                    "  </p>\n",
                            contentSnippet.getTopic(), contentSnippet.getIntro(), contentSnippet.getSummary()) +
                    "\n" +
                    "</speak>");
            chatBubble.setItems(Collections.singletonList(item));

            final GoogleAssistantResponseMessages.ResponseBasicCard responseBasicCard = new GoogleAssistantResponseMessages.ResponseBasicCard();
            responseBasicCard.setTitle(contentSnippet.getTopic());
            responseBasicCard.setSubtitle(contentSnippet.getIntro());
            responseBasicCard.setFormattedText(contentSnippet.getSummary());
            final GoogleAssistantResponseMessages.ResponseBasicCard.Button button = new GoogleAssistantResponseMessages.ResponseBasicCard.Button();
            button.setTitle("Check it on welt");
            final GoogleAssistantResponseMessages.ResponseBasicCard.OpenUrlAction action = new GoogleAssistantResponseMessages.ResponseBasicCard.OpenUrlAction();
            action.setUrl(contentSnippet.getUrl());
            button.setOpenUrlAction(action);
            responseBasicCard.setButtons(Collections.singletonList(button));
            fulfillment.setMessages(Arrays.asList(chatBubble, responseBasicCard));
            //return new ApiGatewayResponse("{\"speech\" : \"hello\", \"contextOut\":[],\"data\":{\"google\":{\"expectUserResponse\":false,\"isSsml\":false,\"noInputPrompts\":[]}}}");
            sessionNewstickerStepRepo.markAsRead(sessionId, contentSnippet.getId());

        } else {
            final GoogleAssistantResponseMessages.ResponseChatBubble.Item item = new GoogleAssistantResponseMessages.ResponseChatBubble.Item();
            item.setTextToSpeech("I do not have more content. Try again later");
            final Map<String, Boolean> map = Collections.singletonMap("expectUserResponse", false);
            fulfillment.getData().put("google", GsonFactory.getDefaultFactory().getGson().toJsonTree(map));
            chatBubble.setItems(Collections.singletonList(item));

            fulfillment.setMessages(Collections.singletonList(chatBubble));
        }

        return new ApiGatewayResponse(GsonFactory.getDefaultFactory().getGson().toJson(fulfillment));
    }


    private Set<ContentSnippet> fetchContent() {
        return frankRepo.fetchContentSnippet(papyrusRepo.fetchIds());
    }

}
