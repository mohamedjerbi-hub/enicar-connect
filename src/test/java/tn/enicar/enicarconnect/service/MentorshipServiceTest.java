package tn.enicar.enicarconnect.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.enicar.enicarconnect.model.MentorshipRequest;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.MentorshipRequestRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MentorshipService mentorshipService;

    @Test
    public void testSendRequest_Success() {
        User mentee = new User();
        mentee.setId(1L);
        mentee.setRole(tn.enicar.enicarconnect.model.Role.STUDENT);
        
        User mentor = new User();
        mentor.setId(2L);
        mentor.setRole(tn.enicar.enicarconnect.model.Role.TEACHER);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(mentee));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(mentor));
        Mockito.when(mentorshipRepository.existsByMentorAndMenteeAndStatusIn(any(), any(), any())).thenReturn(false);
        Mockito.when(mentorshipRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        MentorshipRequest req = MentorshipRequest.builder()
                .mentor(mentor)
                .mentee(mentee)
                .objective("Career Advice")
                .build();

        // The actual signature of requestMentorship
        mentorshipService.requestMentorship(2L, "Career Advice", 1L);
        
        Mockito.verify(mentorshipRepository, Mockito.times(1)).save(any(MentorshipRequest.class));
    }
}
