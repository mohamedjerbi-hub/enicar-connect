package tn.enicar.enicarconnect.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.enicar.enicarconnect.dto.MentorshipDTO;
import tn.enicar.enicarconnect.dto.UserDTO;
import tn.enicar.enicarconnect.model.MentorshipRequest;
import tn.enicar.enicarconnect.model.Role;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.MentorshipRequestRepository;
import tn.enicar.enicarconnect.repository.ChatMessageRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MentorshipService Unit Tests (Senior QA)")
class MentorshipServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private MentorshipService mentorshipService;

    @Test
    @DisplayName("TC-MENTOR-01: Should request mentorship successfully when valid")
    void testSendRequest_Success() {
        User mentee = User.builder().id(1L).role(Role.STUDENT).firstName("Ali").lastName("Ben").build();
        User mentor = User.builder().id(2L).role(Role.TEACHER).firstName("Dr").lastName("Prof").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(mentee));
        when(userRepository.findById(2L)).thenReturn(Optional.of(mentor));
        when(mentorshipRepository.existsByMentorAndMenteeAndStatusIn(any(), any(), any())).thenReturn(false);
        when(mentorshipRepository.save(any(MentorshipRequest.class))).thenAnswer(invocation -> {
            MentorshipRequest r = invocation.getArgument(0);
            r.setId(10L);
            r.setCreatedAt(LocalDateTime.now());
            return r;
        });

        MentorshipDTO dto = mentorshipService.requestMentorship(2L, "Career Advice in AI", 1L);

        assertThat(dto).isNotNull();
        assertThat(dto.getObjective()).isEqualTo("Career Advice in AI");
        assertThat(dto.getStatus()).isEqualTo("PENDING");
        verify(mentorshipRepository).save(any(MentorshipRequest.class));
    }

    @Test
    @DisplayName("TC-MENTOR-02: Should reject mentorship request to oneself")
    void testSendRequest_SelfMentorshipRejected() {
        User user = User.builder().id(1L).role(Role.STUDENT).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> mentorshipService.requestMentorship(1L, "Help myself", 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Vous ne pouvez pas être votre propre mentor");

        verify(mentorshipRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC-MENTOR-03: Should reject duplicate active or pending mentorship request")
    void testSendRequest_DuplicateRequestRejected() {
        User mentee = User.builder().id(1L).role(Role.STUDENT).build();
        User mentor = User.builder().id(2L).role(Role.ALUMNI).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(mentee));
        when(userRepository.findById(2L)).thenReturn(Optional.of(mentor));
        when(mentorshipRepository.existsByMentorAndMenteeAndStatusIn(eq(mentor), eq(mentee), anyList())).thenReturn(true);

        assertThatThrownBy(() -> mentorshipService.requestMentorship(2L, "Second request", 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Demande existante ou déjà active");

        verify(mentorshipRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC-MENTOR-04: Mentor can accept request successfully")
    void testAcceptRequest_Success() {
        User mentor = User.builder().id(2L).role(Role.TEACHER).build();
        User mentee = User.builder().id(1L).role(Role.STUDENT).build();
        MentorshipRequest request = MentorshipRequest.builder()
                .id(100L)
                .mentor(mentor)
                .mentee(mentee)
                .status("PENDING")
                .build();

        when(mentorshipRepository.findById(100L)).thenReturn(Optional.of(request));

        mentorshipService.acceptRequest(100L, 2L);

        assertThat(request.getStatus()).isEqualTo("ACTIVE");
        verify(mentorshipRepository).save(request);
    }

    @Test
    @DisplayName("TC-MENTOR-05: Non-mentor user cannot accept request")
    void testAcceptRequest_UnauthorizedUserThrows() {
        User mentor = User.builder().id(2L).role(Role.TEACHER).build();
        MentorshipRequest request = MentorshipRequest.builder()
                .id(100L)
                .mentor(mentor)
                .status("PENDING")
                .build();

        when(mentorshipRepository.findById(100L)).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> mentorshipService.acceptRequest(100L, 999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Permission refusée");
    }

    @Test
    @DisplayName("TC-MENTOR-06: Should filter and return available ALUMNI and TEACHER mentors")
    void testGetAvailableMentors() {
        User student = User.builder().id(1L).role(Role.STUDENT).email("s@enicar.tn").build();
        User alumni = User.builder().id(2L).role(Role.ALUMNI).email("a@enicar.tn").department("INFO").build();
        User teacher = User.builder().id(3L).role(Role.TEACHER).email("t@enicar.tn").department("GSIL").build();

        when(userRepository.findAll()).thenReturn(List.of(student, alumni, teacher));

        List<UserDTO> mentors = mentorshipService.getAvailableMentors();

        assertThat(mentors).hasSize(2);
        assertThat(mentors).extracting(UserDTO::getEmail).containsExactlyInAnyOrder("a@enicar.tn", "t@enicar.tn");
    }
}
