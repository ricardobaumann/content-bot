package contentbot.dto;

public class AudioLambdaInput {
    private final String ssml;
    private final String bucketName;
    private final String fileName;

    public AudioLambdaInput(final String ssml, final String bucketName, final String fileName) {

        this.ssml = ssml;
        this.bucketName = bucketName;
        this.fileName = fileName;
    }

    public String getSsml() {
        return ssml;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getFileName() {
        return fileName;
    }
}
