package tn.enicar.enicarconnect;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class EnicarConnectApplicationTests {

    @Test
    void contextLoads() {
        // Sanity test: If the Spring application context starts successfully, this test passes.
    }
}
