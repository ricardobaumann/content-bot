package contentbot;

import ai.api.model.Fulfillment;
import ai.api.model.GoogleAssistantResponseMessages;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import contentbot.dto.ApiGatewayRequest;
import contentbot.dto.ApiGatewayResponse;
import contentbot.dto.ContentSnippet;
import contentbot.repo.SessionNewstickerStepRepo;
import contentbot.service.ContentSnippetService;
import contentbot.service.SsmlTranslationService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class NewstickerGoogleActionsHandler implements Loggable {

    private final SessionNewstickerStepRepo sessionNewstickerStepRepo;
    private final ContentSnippetService contentSnippetService;
    private final SsmlTranslationService ssmlTranslationService;
    private final Gson gson;

    NewstickerGoogleActionsHandler(final SessionNewstickerStepRepo sessionNewstickerStepRepo,
                                   final ContentSnippetService contentSnippetService,
                                   final SsmlTranslationService ssmlTranslationService,
                                   final Gson gson) {
        this.sessionNewstickerStepRepo = sessionNewstickerStepRepo;
        this.contentSnippetService = contentSnippetService;
        this.ssmlTranslationService = ssmlTranslationService;
        this.gson = gson;
    }

    ApiGatewayResponse handle(final ApiGatewayRequest apiGatewayRequest) throws IOException {
        final JsonElement jsonElement = gson.fromJson(apiGatewayRequest.getBody(), JsonElement.class);

        final String textValueArgument = jsonElement.getAsJsonObject().get("originalRequest")
                .getAsJsonObject().get("data").getAsJsonObject().get("inputs").getAsJsonArray().get(0)
                .getAsJsonObject().get("arguments").getAsJsonArray().get(0).getAsJsonObject().get("textValue").getAsString();

        if (textValueArgument.toLowerCase().contains("full")) {
            return renderFullAudioResponse();
        } else {
            return renderSnippet(jsonElement);
        }

    }

    private ApiGatewayResponse renderFullAudioResponse() {
        final Fulfillment fulfillment = new Fulfillment();
        final GoogleAssistantResponseMessages.ResponseChatBubble chatBubble = new GoogleAssistantResponseMessages.ResponseChatBubble();
        chatBubble.setCustomizeAudio(true);
        final GoogleAssistantResponseMessages.ResponseChatBubble.Item item = new GoogleAssistantResponseMessages.ResponseChatBubble.Item();
        item.setSsml(ssmlTranslationService.getFullAudioSsmlResponse());
        chatBubble.setItems(Collections.singletonList(item));
        fulfillment.setMessages(Collections.singletonList(chatBubble));
        return new ApiGatewayResponse(gson.toJson(fulfillment));
    }

    private ApiGatewayResponse renderSnippet(final JsonElement jsonElement) {
        final String sessionId = jsonElement.getAsJsonObject().get("sessionId").getAsString();
        final Fulfillment fulfillment = new Fulfillment();
        final GoogleAssistantResponseMessages.ResponseChatBubble chatBubble = new GoogleAssistantResponseMessages.ResponseChatBubble();
        chatBubble.setCustomizeAudio(true);
        final Set<ContentSnippet> snippets = contentSnippetService.getContentSnippets();
        final Optional<ContentSnippet> contentSnippetOptional = snippets
                .stream()
                .filter(cs -> !sessionNewstickerStepRepo.getReadIds(sessionId).contains(cs.getId())).findFirst();

        if (contentSnippetOptional.isPresent()) {
            final ContentSnippet contentSnippet = contentSnippetOptional.get();
            logger().info("Delivering snippet: {}", contentSnippet.getId());
            final GoogleAssistantResponseMessages.ResponseChatBubble.Item item = new GoogleAssistantResponseMessages.ResponseChatBubble.Item();
            item.setSsml(ssmlTranslationService.asSSML(contentSnippet));
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
            sessionNewstickerStepRepo.markAsRead(sessionId, contentSnippet.getId());

        } else {
            final GoogleAssistantResponseMessages.ResponseChatBubble.Item item = new GoogleAssistantResponseMessages.ResponseChatBubble.Item();
            item.setTextToSpeech("I do not have more content. Try again later");
            final Map<String, Boolean> map = Collections.singletonMap("expectUserResponse", false);
            if (fulfillment.getData() == null) {
                fulfillment.setData(new HashMap<>());
            }
            fulfillment.getData().put("google", gson.toJsonTree(map));
            chatBubble.setItems(Collections.singletonList(item));

            fulfillment.setMessages(Collections.singletonList(chatBubble));
        }
        return new ApiGatewayResponse(gson.toJson(fulfillment));
    }


}
