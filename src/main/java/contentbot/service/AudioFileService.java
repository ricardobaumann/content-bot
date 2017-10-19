package contentbot.service;

import com.amazonaws.services.lambda.AWSLambdaAsync;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import contentbot.Loggable;
import contentbot.config.AudioFileProperties;
import contentbot.config.AudioLambdaProperties;
import contentbot.dto.AudioLambdaInput;
import contentbot.dto.ContentSnippet;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AudioFileService implements Loggable {

    private final ContentSnippetService contentSnippetService;
    private final SsmlTranslationService ssmlTranslationService;
    private final AWSLambdaAsync lambda;
    private final AudioLambdaProperties audioLambdaProperties;
    private final AudioFileProperties audioFileProperties;
    private final ObjectMapper objectMapper;
    private final AmazonS3 amazonS3Client;

    AudioFileService(final ContentSnippetService contentSnippetService,
                     final SsmlTranslationService ssmlTranslationService,
                     final AWSLambdaAsync lambda,
                     final AudioLambdaProperties audioLambdaProperties,
                     final AudioFileProperties audioFileProperties,
                     final ObjectMapper objectMapper,
                     final AmazonS3 amazonS3Client) {
        this.contentSnippetService = contentSnippetService;
        this.ssmlTranslationService = ssmlTranslationService;
        this.lambda = lambda;
        this.audioLambdaProperties = audioLambdaProperties;
        this.audioFileProperties = audioFileProperties;
        this.objectMapper = objectMapper;
        this.amazonS3Client = amazonS3Client;
    }

    public void generateAudioFile() {
        final Set<ContentSnippet> snippets = contentSnippetService.getContentSnippets();
        snippets.forEach(contentSnippet -> {
            final String ssml = ssmlTranslationService.asSSML(contentSnippet);
            try {
                final InvokeRequest invokeRequest = new InvokeRequest()
                        .withFunctionName(audioLambdaProperties.getFunctionName())
                        .withInvocationType(InvocationType.RequestResponse)
                        .withPayload(objectMapper.writeValueAsString(new AudioLambdaInput(ssml, audioFileProperties.getTargetS3Bucket(), contentSnippet.getId().concat(".mp3"), audioFileProperties.getRegion())));

                final InvokeResult result = lambda.invoke(invokeRequest);
                logger().info("Lambda invoked with result status {} and payload {}", result.getStatusCode(), new String(result.getPayload().array()));

            } catch (final JsonProcessingException e) {
                logger().error("Failed to generate audio file", e);
            }
        });

    }

}
