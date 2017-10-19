package contentbot.service;

import contentbot.dto.ContentSnippet;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SsmlTranslationService {

    private static final String SSML_HEADER = "<speak xmlns=\"http://www.w3.org/2001/10/synthesis\"\n" +
            "       xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n" +
            "       version=\"1.0\">\n" +
            "  <metadata>\n" +
            "    <dc:title xml:lang=\"en\">Content qcu summary</dc:title>\n" +
            "  </metadata>\n";

    private static final String SSML_FOOTER = "</speak>";

    private static final String SSML_PARAGRAPH_TEMPLATE =
            "\n" +
                    "  <p>\n" +
                    "    <s xml:lang=\"de-DE\">\n" +
                    "        <emphasis>%s</emphasis> <break time=\"2s\" /> %s <break time=\"2s\" /> %s\n" +
                    "    </s>\n" +
                    "  </p>\n" +
                    "\n";

    public String asSSML(final ContentSnippet contentSnippet) {
        return String.format(SSML_HEADER + SSML_PARAGRAPH_TEMPLATE + SSML_FOOTER, contentSnippet.getTopic(), contentSnippet.getIntro(), contentSnippet.getSummary());
    }

    public String asSSML(final Set<ContentSnippet> contentSnippets) {
        return SSML_HEADER
                + (contentSnippets.stream().map(contentSnippet -> String.format(SSML_PARAGRAPH_TEMPLATE, contentSnippet.getTopic(), contentSnippet.getIntro(), contentSnippet.getSummary()))
                .collect(Collectors.joining()))
                + SSML_FOOTER;
    }

}
