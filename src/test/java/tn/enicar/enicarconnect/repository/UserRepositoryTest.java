package tn.enicar.enicarconnect.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tn.enicar.enicarconnect.model.Role;
import tn.enicar.enicarconnect.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

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