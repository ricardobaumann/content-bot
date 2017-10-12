package contentbot;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class FrankRepoTest {

    @Autowired
    private FrankRepo frankRepo;

    @Test
    public void fetchQcus() throws Exception {
        final Set<String> qcus = frankRepo.fetchQcus(Sets.newHashSet("169565664", "169566800"));
        assertThat(qcus).isNotEmpty();
    }

}