package tn.enicar.enicarconnect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import tn.enicar.enicarconnect.dto.JobDTO;
import tn.enicar.enicarconnect.model.JobOffer;
import tn.enicar.enicarconnect.repository.UserRepository;
import tn.enicar.enicarconnect.service.JobService;
import tn.enicar.enicarconnect.security.JwtAuthFilter;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JobController.class)
@AutoConfigureMockMvc(addFilters = false) // By-pass fully JWT filter security configuration for simple controller tests
public class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobService jobService;
    
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtAuthFilter jwtAuthFilter; // Optional if filters are disabled but context demands it

    @Test
    @WithMockUser
    public void testGetAllJobs() throws Exception {
        JobDTO jobDTO = JobDTO.builder()
                .title("Software Engineer")
                .company("Tech Corp")
                .build();

        Mockito.when(userRepository.findByEmail(any())).thenReturn(java.util.Optional.of(new tn.enicar.enicarconnect.model.User() { { setId(1L); } }));
        Mockito.when(jobService.getAllJobs(anyLong())).thenReturn(Collections.singletonList(jobDTO));

        org.springframework.security.core.Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("user", "pass");

        mockMvc.perform(get("/api/jobs")
                        .principal(auth)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Software Engineer"));
    }

    @Test
    @WithMockUser
    public void testCreateJob() throws Exception {
        JobOffer jobOffer = new JobOffer();
        jobOffer.setTitle("Backend Developer");
        
        JobDTO jobDTO = JobDTO.builder()
                .title("Backend Developer")
                .company("Startup Inc")
                .build();

        Mockito.when(userRepository.findByEmail(any())).thenReturn(java.util.Optional.of(new tn.enicar.enicarconnect.model.User() { { setId(1L); } }));
        Mockito.when(jobService.createJob(any(), anyLong())).thenReturn(jobDTO);

        ObjectMapper mapper = new ObjectMapper();

        org.springframework.security.core.Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("user", "pass");

        mockMvc.perform(post("/api/jobs")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(jobOffer)))
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.title").value("Backend Developer"));
    }
}
