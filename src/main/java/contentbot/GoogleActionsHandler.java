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
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
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
        final JsonNode inputJsonNode = objectMapper.readTree(apiGatewayRequest.getBody());
        final String contentQuery = inputJsonNode.get("result").get("parameters").get("content").asText();
        final Fulfillment fulfillment = new Fulfillment();
        //fulfillment.setSpeech("hello");
        final GoogleAssistantResponseMessages.ResponseChatBubble chatBubble = new GoogleAssistantResponseMessages.ResponseChatBubble();
        chatBubble.setCustomizeAudio(true);
        //item.setTextToSpeech("text to speech");
        final Set<ContentSnippet> snippets = fetchContent(contentQuery);
        final GoogleAssistantResponseMessages.ResponseChatBubble.Item item = new GoogleAssistantResponseMessages.ResponseChatBubble.Item();
        item.setSsml("<speak xmlns=\"http://www.w3.org/2001/10/synthesis\"\n" +
                "       xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n" +
                "       version=\"1.0\">\n" +
                "  <metadata>\n" +
                "    <dc:title xml:lang=\"en\">Content qcu summary</dc:title>\n" +
                "  </metadata>\n" +
                "\n" +
                buildVoiceSnippets(snippets) +
                "\n" +
                "</speak>");
        chatBubble.setItems(Arrays.asList(item));

        final GoogleAssistantResponseMessages.ResponseBasicCard responseBasicCard = new GoogleAssistantResponseMessages.ResponseBasicCard();
        final ContentSnippet contentSnippet = snippets.iterator().next();
        responseBasicCard.setTitle(contentSnippet.getTopic());
        responseBasicCard.setSubtitle(contentSnippet.getIntro());
        responseBasicCard.setFormattedText(contentSnippet.getSummary());


        fulfillment.setMessages(Arrays.asList(chatBubble, responseBasicCard));
        //return new ApiGatewayResponse("{\"speech\" : \"hello\", \"contextOut\":[],\"data\":{\"google\":{\"expectUserResponse\":false,\"isSsml\":false,\"noInputPrompts\":[]}}}");
        return new ApiGatewayResponse(GsonFactory.getDefaultFactory().getGson().toJson(fulfillment));
    }

    private String buildVoiceSnippets(final Set<ContentSnippet> snippets) {
        return snippets.stream().map(contentSnippet -> String.format(
                "  <p>\n" +
                        "    <s xml:lang=\"de-DE\">\n" +
                        "      <voice name=\"David\" gender=\"male\" age=\"25\">\n" +
                        "        <emphasis>%s</emphasis> <break time=\"2s\" /> %s <break time=\"2s\" /> %s\n" +
                        "      </voice>\n" +
                        "    </s>\n" +
                        "  </p>\n",
                contentSnippet.getTopic(), contentSnippet.getIntro(), contentSnippet.getSummary())).collect(Collectors.joining());

    }

    private Set<ContentSnippet> fetchContent(final String contentQuery) {
        return frankRepo.fetchContentSnippet(papyrusRepo.fetchIds(contentQuery));
    }

}
