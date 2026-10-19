package tn.enicar.enicarconnect.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.enicar.enicarconnect.dto.UserDTO;
import tn.enicar.enicarconnect.model.ConnectionRequest;
import tn.enicar.enicarconnect.model.Role;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.ConnectionRequestRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConnectionService Unit Tests (Senior QA)")
class ConnectionServiceTest {

    @Mock
    private ConnectionRequestRepository connectionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ConnectionService connectionService;

    @Test
    @DisplayName("TC-CONN-01: Should send connection request successfully")
    void shouldSendConnectionRequestSuccessfully() {
        User sender = User.builder().id(1L).email("user1@enicar.tn").build();
        User receiver = User.builder().id(2L).email("user2@enicar.tn").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(connectionRepository.existsBySenderAndReceiver(sender, receiver)).thenReturn(false);
        when(connectionRepository.existsBySenderAndReceiver(receiver, sender)).thenReturn(false);

        connectionService.sendRequest(1L, 2L);

        verify(connectionRepository).save(any(ConnectionRequest.class));
    }

    @Test
    @DisplayName("TC-CONN-02: Should reject connection request to self")
    void shouldRejectSelfConnection() {
        User user = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> connectionService.sendRequest(1L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Vous ne pouvez pas vous connecter à vous-même");

        verify(connectionRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC-CONN-03: Should reject duplicate connection request")
    void shouldRejectDuplicateConnectionRequest() {
        User sender = User.builder().id(1L).build();
        User receiver = User.builder().id(2L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(connectionRepository.existsBySenderAndReceiver(sender, receiver)).thenReturn(true);

        assertThatThrownBy(() -> connectionService.sendRequest(1L, 2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Une relation ou demande existe déjà");

        verify(connectionRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC-CONN-04: Should accept pending request by receiver")
    void shouldAcceptRequestSuccessfully() {
        User receiver = User.builder().id(2L).build();
        ConnectionRequest req = ConnectionRequest.builder().id(10L).receiver(receiver).status("PENDING").build();

        when(connectionRepository.findById(10L)).thenReturn(Optional.of(req));

        connectionService.acceptRequest(10L, 2L);

        assertThat(req.getStatus()).isEqualTo("ACCEPTED");
        verify(connectionRepository).save(req);
    }

    @Test
    @DisplayName("TC-CONN-05: Should reject accept attempt by non-receiver user")
    void shouldRejectAcceptByNonReceiver() {
        User receiver = User.builder().id(2L).build();
        ConnectionRequest req = ConnectionRequest.builder().id(10L).receiver(receiver).status("PENDING").build();

        when(connectionRepository.findById(10L)).thenReturn(Optional.of(req));

        assertThatThrownBy(() -> connectionService.acceptRequest(10L, 999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Action non autorisée");
    }

    @Test
    @DisplayName("TC-CONN-06: Should retrieve accepted network contacts")
    void shouldGetMyNetwork() {
        User me = User.builder().id(1L).build();
        User friend = User.builder().id(2L).firstName("Amine").lastName("S").role(Role.STUDENT).email("amine@enicar.tn").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(me));
        when(connectionRepository.findAcceptedConnections(me)).thenReturn(List.of(friend));

        List<UserDTO> network = connectionService.getMyNetwork(1L);

        assertThat(network).hasSize(1);
        assertThat(network.get(0).getEmail()).isEqualTo("amine@enicar.tn");
    }
}
