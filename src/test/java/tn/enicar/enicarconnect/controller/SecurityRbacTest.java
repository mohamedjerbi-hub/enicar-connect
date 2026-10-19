package tn.enicar.enicarconnect.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tn.enicar.enicarconnect.service.DocumentService;
import tn.enicar.enicarconnect.service.UserService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Security & RBAC Integration Tests (Senior QA)")
class SecurityRbacTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private DocumentService documentService;

    @Test
    @DisplayName("TC-RBAC-01: Anonymous request to protected /api/users must be rejected (401/403)")
    void anonymousAccessToAdminEndpointShouldBeRejected() throws Exception {
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "student@enicar.tn", authorities = {"ROLE_STUDENT", "POST_READ"})
    @DisplayName("TC-RBAC-02: STUDENT role accessing Admin GET /api/users must return 403 Forbidden")
    void studentAccessToAdminEndpointMustReturn403() throws Exception {
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "teacher@enicar.tn", authorities = {"ROLE_TEACHER", "POST_READ"})
    @DisplayName("TC-RBAC-03: TEACHER role accessing Admin GET /api/users must return 403 Forbidden")
    void teacherAccessToAdminEndpointMustReturn403() throws Exception {
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "student@enicar.tn", authorities = {"ROLE_STUDENT"})
    @DisplayName("TC-RBAC-04: STUDENT role approving document PUT /api/documents/1/approve must return 403 Forbidden")
    void studentApprovingDocumentMustReturn403() throws Exception {
        mockMvc.perform(put("/api/documents/1/approve")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@enicar.tn", authorities = {"ROLE_ADMIN_STAFF", "USER_MANAGE"})
    @DisplayName("TC-RBAC-05: ADMIN_STAFF role accessing GET /api/users must return 200 OK")
    void adminStaffAccessToUsersMustReturn200() throws Exception {
        when(userService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "director@enicar.tn", authorities = {"ROLE_DIRECTION", "USER_MANAGE"})
    @DisplayName("TC-RBAC-06: DIRECTION role accessing GET /api/users must return 200 OK")
    void directionAccessToUsersMustReturn200() throws Exception {
        when(userService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "student@enicar.tn", authorities = {"ROLE_STUDENT"})
    @DisplayName("TC-RBAC-07: STUDENT role accessing shared directory /api/users/directory must return 200 OK")
    void studentAccessToDirectoryMustReturn200() throws Exception {
        when(userService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/users/directory")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
