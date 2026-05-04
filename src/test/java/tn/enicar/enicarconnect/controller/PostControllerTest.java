package tn.enicar.enicarconnect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tn.enicar.enicarconnect.support.AbstractPostgresIntegrationTest;
import org.springframework.transaction.annotation.Transactional;
import tn.enicar.enicarconnect.dto.CreatePostRequest;
import tn.enicar.enicarconnect.model.Role;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "postgres"})
@Transactional
class PostControllerTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private static final String TEST_USER_EMAIL = "post.author@test.com";

    @BeforeEach
    void setup() {
        if (userRepository.findByEmail(TEST_USER_EMAIL).isEmpty()) {
            User user = User.builder()
                    .firstName("Post")
                    .lastName("Author")
                    .email(TEST_USER_EMAIL)
                    .password("pass")
                    .role(Role.STUDENT)
                    .build();
            userRepository.save(user);
        }
    }

    @Test
    @WithMockUser(username = TEST_USER_EMAIL, authorities = {"ROLE_STUDENT"})
    void shouldCreatePostSuccessfully() throws Exception {
        CreatePostRequest request = new CreatePostRequest();
        request.setBody("This is my first test post! #testing");
        request.setVisibility("PUBLIC");

        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").value("This is my first test post! #testing"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(username = TEST_USER_EMAIL, authorities = {"ROLE_STUDENT"})
    void shouldGetAllPosts() throws Exception {
        // Fetch posts
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
