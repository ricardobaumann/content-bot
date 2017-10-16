package contentbot.dto;

public class ContentSnippet {
    private final String topic;
    private final String intro;
    private final String summary;

    public ContentSnippet(final String topic, final String intro, final String summary) {
        this.topic = topic;
        this.intro = intro;
        this.summary = summary;
    }

    public String getTopic() {
        return topic;
    }

    public String getIntro() {
        return intro;
    }

    public String getSummary() {
        return summary;
    }
}
