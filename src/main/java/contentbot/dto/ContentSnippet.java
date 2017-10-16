package contentbot.dto;

public class ContentSnippet {
    private final String topic;
    private final String intro;
    private final String summary;
    private final String url;

    public ContentSnippet(final String topic, final String intro, final String summary, final String url) {
        this.topic = topic;
        this.intro = intro;
        this.summary = summary;
        this.url = url;
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

    public String getUrl() {
        return url;
    }
}
