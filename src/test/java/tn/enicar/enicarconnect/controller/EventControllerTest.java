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
import tn.enicar.enicarconnect.dto.AppEventDTO;
import tn.enicar.enicarconnect.model.AppEvent;
import tn.enicar.enicarconnect.repository.UserRepository;
import tn.enicar.enicarconnect.service.EventService;
import tn.enicar.enicarconnect.security.JwtAuthFilter;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @WithMockUser
    public void testGetAllEvents() throws Exception {
        AppEventDTO eventDTO = AppEventDTO.builder()
                .title("Tech Conference")
                .location("Auditorium")
                .build();

        Mockito.when(userRepository.findByEmail(any())).thenReturn(java.util.Optional.of(new tn.enicar.enicarconnect.model.User() { { setId(1L); } }));
        Mockito.when(eventService.getAllEvents(anyLong())).thenReturn(Collections.singletonList(eventDTO));

        org.springframework.security.core.Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("user", "pass");

        mockMvc.perform(get("/api/events")
                        .principal(auth)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Tech Conference"));
    }

    @Test
    @WithMockUser
    public void testCreateEvent() throws Exception {
        AppEvent event = new AppEvent();
        event.setTitle("Workshop Hackathon");
        
        AppEventDTO eventDTO = AppEventDTO.builder()
                .title("Workshop Hackathon")
                .build();

        Mockito.when(userRepository.findByEmail(any())).thenReturn(java.util.Optional.of(new tn.enicar.enicarconnect.model.User() { { setId(1L); } }));
        Mockito.when(eventService.createEvent(any(), anyLong())).thenReturn(eventDTO);

        ObjectMapper mapper = new ObjectMapper();

        org.springframework.security.core.Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("user", "pass");

        mockMvc.perform(post("/api/events")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Workshop Hackathon"));
    }
}
