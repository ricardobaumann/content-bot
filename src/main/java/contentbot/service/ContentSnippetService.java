package contentbot.service;

import contentbot.dto.ContentSnippet;
import contentbot.repo.FrankRepo;
import contentbot.repo.PapyrusRepo;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ContentSnippetService {

    private final FrankRepo frankRepo;
    private final PapyrusRepo papyrusRepo;

    ContentSnippetService(final FrankRepo frankRepo, final PapyrusRepo papyrusRepo) {
        this.frankRepo = frankRepo;
        this.papyrusRepo = papyrusRepo;
    }

    public Set<ContentSnippet> getContentSnippets() {
        return frankRepo.fetchContentSnippet(papyrusRepo.fetchIds());
    }

}
