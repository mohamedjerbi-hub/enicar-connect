package tn.enicar.enicarconnect.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.enicar.enicarconnect.dto.AppEventDTO;
import tn.enicar.enicarconnect.model.AppEvent;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.EventRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventService Unit Tests (Senior QA)")
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    @DisplayName("TC-EVENT-01: Should create event assigned to current user")
    void shouldCreateEvent() {
        User owner = User.builder().id(1L).email("organizer@enicar.tn").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        AppEvent event = AppEvent.builder()
                .title("Forum ENICAR 2026")
                .date("2026-11-20")
                .time("09:00")
                .registeredUsers(new HashSet<>())
                .build();

        when(eventRepository.save(any(AppEvent.class))).thenAnswer(i -> {
            AppEvent e = i.getArgument(0);
            e.setId(101L);
            return e;
        });

        AppEventDTO dto = eventService.createEvent(event, 1L);

        assertThat(dto).isNotNull();
        assertThat(dto.getTitle()).isEqualTo("Forum ENICAR 2026");
        assertThat(dto.isOwner()).isTrue();
        verify(eventRepository).save(event);
    }

    @Test
    @DisplayName("TC-EVENT-02: Should register user when capacity allows")
    void shouldRegisterUserWhenCapacityAllows() {
        User user = User.builder().id(2L).build();
        AppEvent event = AppEvent.builder()
                .id(10L)
                .title("Hackathon")
                .maxCapacity(10)
                .registeredUsers(new HashSet<>())
                .build();

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(eventRepository.save(any(AppEvent.class))).thenAnswer(i -> i.getArgument(0));

        AppEventDTO dto = eventService.toggleRegister(10L, 2L);

        assertThat(dto.isRegistered()).isTrue();
        assertThat(event.getRegisteredUsers()).contains(user);
    }

    @Test
    @DisplayName("TC-EVENT-03: Should reject registration when maxCapacity reached")
    void shouldRejectRegistrationWhenFull() {
        User existingUser = User.builder().id(3L).build();
        User newUser = User.builder().id(4L).build();

        HashSet<User> registered = new HashSet<>();
        registered.add(existingUser);

        AppEvent event = AppEvent.builder()
                .id(10L)
                .maxCapacity(1)
                .registeredUsers(registered)
                .build();

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(userRepository.findById(4L)).thenReturn(Optional.of(newUser));

        assertThatThrownBy(() -> eventService.toggleRegister(10L, 4L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Événement complet");
    }

    @Test
    @DisplayName("TC-EVENT-04: Should unregister user when already registered")
    void shouldUnregisterWhenAlreadyRegistered() {
        User user = User.builder().id(2L).build();
        HashSet<User> registered = new HashSet<>();
        registered.add(user);

        AppEvent event = AppEvent.builder()
                .id(10L)
                .registeredUsers(registered)
                .build();

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(eventRepository.save(any(AppEvent.class))).thenAnswer(i -> i.getArgument(0));

        AppEventDTO dto = eventService.toggleRegister(10L, 2L);

        assertThat(dto.isRegistered()).isFalse();
        assertThat(event.getRegisteredUsers()).doesNotContain(user);
    }

    @Test
    @DisplayName("TC-EVENT-05: Non-owner cannot delete event")
    void shouldRejectDeleteByNonOwner() {
        User owner = User.builder().id(1L).build();
        AppEvent event = AppEvent.builder().id(10L).owner(owner).build();

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.deleteEvent(10L, 999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unauthorized");

        verify(eventRepository, never()).delete(any());
    }
}
