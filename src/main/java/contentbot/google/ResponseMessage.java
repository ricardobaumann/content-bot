/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package contentbot.google;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base model class for
 * <a href="https://docs.api.ai/docs/query#section-message-objects">response message objects</a>.
 */
public abstract class ResponseMessage {

    @Expose
    private final MessageType type;

    @Expose
    private final Platform platform;

    /**
     * Constructor initializing message type code
     *
     * @param type Message type. Cannot be <code>null</code>.
     */
    protected ResponseMessage(final MessageType type) {
        this(type, null);
    }

    /**
     * Constructor initializing message type code and platform
     *
     * @param type     Message type. Cannot be <code>null</code>.
     * @param platform Platform type. If <code>null</code> then
     *                 default value will be used
     */
    protected ResponseMessage(final MessageType type, final Platform platform) {
        assert type != null;
        this.type = type;
        this.platform = platform != null ? platform : Platform.DEFAULT;
    }

    /**
     * Holds the message type integer code and related {@link Type}
     */
    public static enum MessageType {
        /**
         * Text response message object
         */
        SPEECH(0, "message", ResponseSpeech.class),
        /**
         * Card message object
         */
        CARD(1, "card", ResponseCard.class),
        /**
         * Quick replies message object
         */
        QUICK_REPLY(2, "quick_reply", ResponseQuickReply.class),
        /**
         * Image message object
         */
        IMAGE(3, "image", ResponseImage.class),
        /**
         * Custom payload message object
         */
        PAYLOAD(4, "custom_payload", ResponsePayload.class),
        CHAT_BUBBLE(5, "simple_response", GoogleAssistantResponseMessages.ResponseChatBubble.class),
        BASIC_CARD(6, "basic_card", GoogleAssistantResponseMessages.ResponseBasicCard.class),
        LIST_CARD(7, "list_card", GoogleAssistantResponseMessages.ResponseListCard.class),
        SUGGESTION_CHIPS(8, "suggestion_chips", GoogleAssistantResponseMessages.ResponseSuggestionChips.class),
        CAROUSEL_CARD(9, "carousel_card", GoogleAssistantResponseMessages.ResponseCarouselCard.class),
        LINK_OUT_CHIP(10, "link_out_chip", GoogleAssistantResponseMessages.ResponseLinkOutChip.class);

        private final int code;
        private final String name;
        private final Type type;

        private MessageType(final int code, final String name, final Type curClass) {
            assert name != null;
            assert curClass != null;
            this.code = code;
            this.name = name;
            this.type = curClass;
        }

        /**
         * @return Message integer code value
         */
        public int getCode() {
            return this.code;
        }

        /**
         * @return Type name presentation
         */
        public String getName() {
            return name;
        }

        /**
         * @return Related class {@link Type}
         */
        public Type getType() {
            return type;
        }


        private static final Map<Integer, MessageType> typeByCode = new HashMap<>();
        private static final Map<String, MessageType> typeByName = new HashMap<>();

        static {
            for (final MessageType type : values()) {
                typeByCode.put(type.code, type);
                typeByName.put(type.name.toLowerCase(), type);
            }
        }

        public static MessageType fromCode(final int code) {
            return typeByCode.get(code);
        }

        public static MessageType fromName(final String name) {
            return typeByName.get(name != null ? name.toLowerCase() : null);
        }
    }

    public enum Platform {
        DEFAULT(null),
        GOOGLE("google"),
        FACEBOOK("facebook"),
        SLACK("slack"),
        TELEGRAM("telegram"),
        KIK("kik"),
        VIBER("viber"),
        SKYPE("skype"),
        LINE("line");

        private final String name;

        Platform(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        private static final Map<String, Platform> platformByName = new HashMap<>();

        static {
            for (final Platform platform : values()) {
                final String platformName = platform.getName();
                platformByName.put(platformName != null ? platformName.toLowerCase() : null, platform);
            }
        }

        public static Platform fromName(final String name) {
            return platformByName.get(name != null ? name.toLowerCase() : null);
        }
    }

    /**
     * <a href="https://docs.api.ai/docs/query#section-text-response-message-object">Text response</a>
     * message object
     */
    public static class ResponseSpeech extends ResponseMessage {

        @Expose
        public List<String> speech;

        public ResponseSpeech() {
            super(MessageType.SPEECH);
        }

        /**
         * Get agent's text replies.
         */
        public List<String> getSpeech() {
            return this.speech;
        }

        /**
         * Set agent's text replies.
         */
        public void setSpeech(final List<String> speech) {
            this.speech = speech;
        }

        /**
         * Set agent's text replies.
         */
        public void setSpeech(final String... speech) {
            setSpeech(Arrays.asList(speech));
        }
    }

    /**
     * <a href="https://docs.api.ai/docs/query#section-card-message-object">Card message</a> object
     */
    public static class ResponseCard extends ResponseMessage {

        @Expose
        private String title;

        @Expose
        private String subtitle;

        @Expose
        private String imageUrl;

        @Expose
        private List<Button> buttons;

        public ResponseCard() {
            super(MessageType.CARD);
        }

        /**
         * Get card title.
         */
        public String getTitle() {
            return this.title;
        }

        /**
         * Set card title.
         */
        public void setTitle(final String title) {
            this.title = title;
        }

        /**
         * Get card subtitle.
         */
        public String getSubtitle() {
            return this.subtitle;
        }

        /**
         * Set card subtitle.
         */
        public void setSubtitle(final String subtitle) {
            this.subtitle = subtitle;
        }

        /**
         * Get image url
         */
        public String getImageUrl() {
            return this.imageUrl;
        }

        /**
         * Set image url
         */
        public void setImageUrl(final String imageUrl) {
            this.imageUrl = imageUrl;
        }

        /**
         * Get list of objects corresponding to card buttons.
         */
        public List<Button> getButtons() {
            return this.buttons;
        }

        /**
         * Set list of objects corresponding to card buttons.
         */
        public void setButtons(final List<Button> buttons) {
            this.buttons = buttons;
        }

        /**
         * Set sequence of objects corresponding to card buttons.
         */
        public void setButtons(final Button... buttons) {
            setButtons(Arrays.asList(buttons));
        }

        /**
         * Card button object
         */
        public static class Button {

            @Expose
            private String text;

            @Expose
            private String postback;

            public Button(final String text, final String postback) {
                this.text = text;
                this.postback = postback;
            }

            /**
             * Set button text
             */
            public void setText(final String text) {
                this.text = text;
            }

            /**
             * Get button text
             */
            public String getText() {
                return this.text;
            }

            /**
             * Set a text sent back to API.AI or a URL to open.
             */
            public void setPostback(final String postback) {
                this.postback = postback;
            }

            /**
             * Get a text sent back to API.AI or a URL to open.
             */
            public String getPostback() {
                return this.postback;
            }
        }
    }

    /**
     * <a href="https://docs.api.ai/docs/query#section-quick-replies-message-object">Quick
     * replies</a> message object
     */
    public static class ResponseQuickReply extends ResponseMessage {

        @Expose
        private String title;

        @Expose
        private List<String> replies;

        public ResponseQuickReply() {
            super(MessageType.QUICK_REPLY);
        }

        /**
         * Get list of text replies
         */
        public List<String> getReplies() {
            return this.replies;
        }

        /**
         * Set list of text replies
         */
        public void setReplies(final List<String> replies) {
            this.replies = replies;
        }

        /**
         * Set sequence of text replies
         */
        public void setReplies(final String... replies) {
            setReplies(Arrays.asList(replies));
        }

        /**
         * Set quick replies title. Required for the Facebook Messenger, Kik, and Telegram one-click
         * integrations.
         */
        public void setTitle(final String title) {
            this.title = title;
        }

        /**
         * Get quick replies title. Required for the Facebook Messenger, Kik, and Telegram one-click
         * integrations.
         */
        public String getTitle() {
            return this.title;
        }
    }

    /**
     * <a href="https://docs.api.ai/docs/query#section-image-message-object">Image</a>  message object
     */
    public static class ResponseImage extends ResponseMessage {

        @Expose
        private String imageUrl;

        public ResponseImage() {
            super(MessageType.IMAGE);
        }

        /**
         * Get image url
         */
        public String getImageUrl() {
            return this.imageUrl;
        }

        /**
         * Set image url
         */
        public void setImageUrl(final String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    /**
     * <a href="">Custom payload</a> message object that holds an object passed through without
     * modification
     */
    public static class ResponsePayload extends ResponseMessage {

        @Expose
        private JsonObject payload;

        public ResponsePayload() {
            super(MessageType.PAYLOAD);
        }

        /**
         * Get custom defined JSON.
         */
        public JsonObject getPayload() {
            return this.payload;
        }

        /**
         * Set custom defined JSON.
         */
        public void setPayload(final JsonObject payload) {
            this.payload = payload;
        }
    }
}
