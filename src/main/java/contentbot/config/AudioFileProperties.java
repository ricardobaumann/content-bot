package contentbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "audio-file")
public class AudioFileProperties {
    private String targetS3Bucket;

    private String targetS3File;

    private String region;

    public String getTargetS3Bucket() {
        return targetS3Bucket;
    }

    public void setTargetS3Bucket(final String targetS3Bucket) {
        this.targetS3Bucket = targetS3Bucket;
    }

    public String getTargetS3File() {
        return targetS3File;
    }

    public void setTargetS3File(final String targetS3File) {
        this.targetS3File = targetS3File;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(final String region) {
        this.region = region;
    }
}
