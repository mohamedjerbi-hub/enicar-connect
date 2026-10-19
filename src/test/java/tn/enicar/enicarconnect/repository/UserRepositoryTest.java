package tn.enicar.enicarconnect.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tn.enicar.enicarconnect.model.Role;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.support.AbstractPostgresIntegrationTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"test", "postgres"})
@Transactional
class UserRepositoryTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindExistingUserByEmail() {
        User user = User.builder()
                .firstName("Test")
                .lastName("Repository")
                .email("repo@enicar.ucar.tn")
                .password("secret")
                .role(Role.STUDENT)
                .build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("repo@enicar.ucar.tn");

        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Test");
    }
}
