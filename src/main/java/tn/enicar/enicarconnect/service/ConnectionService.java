package tn.enicar.enicarconnect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicar.enicarconnect.dto.UserDTO;
import tn.enicar.enicarconnect.model.ConnectionRequest;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.ConnectionRequestRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConnectionService {

    private final ConnectionRequestRepository connectionRepository;
    private final UserRepository userRepository;

    public void sendRequest(Long senderId, Long receiverId) {
        User sender = userRepository.findById(senderId).orElseThrow();
        User receiver = userRepository.findById(receiverId).orElseThrow();

        if (sender.getId().equals(receiver.getId())) {
            throw new RuntimeException("Vous ne pouvez pas vous connecter à vous-même.");
        }

        // Vérifie si une requête existe déjà dans un sens ou dans l'autre
        if (connectionRepository.existsBySenderAndReceiver(sender, receiver) ||
                connectionRepository.existsBySenderAndReceiver(receiver, sender)) {
            throw new RuntimeException("Une relation ou demande existe déjà.");
        }

        ConnectionRequest request = ConnectionRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status("PENDING")
                .build();

        connectionRepository.save(request);
    }

    public void acceptRequest(Long requestId, Long currentUserId) {
        ConnectionRequest request = connectionRepository.findById(requestId).orElseThrow();

        if (!request.getReceiver().getId().equals(currentUserId)) {
            throw new RuntimeException("Action non autorisée.");
        }

        request.setStatus("ACCEPTED");
        connectionRepository.save(request);
    }

    public void rejectRequest(Long requestId, Long currentUserId) {
        ConnectionRequest request = connectionRepository.findById(requestId).orElseThrow();

        if (!request.getReceiver().getId().equals(currentUserId)) {
            throw new RuntimeException("Action non autorisée.");
        }

        request.setStatus("REJECTED");
        connectionRepository.save(request);
    }

    public List<UserDTO> getMyNetwork(Long currentUserId) {
        User user = userRepository.findById(currentUserId).orElseThrow();
        return connectionRepository.findAcceptedConnections(user).stream()
                .map(this::mapUserToDTO)
                .collect(Collectors.toList());
    }

    public List<ConnectionRequest> getPendingRequests(Long currentUserId) {
        User user = userRepository.findById(currentUserId).orElseThrow();
        return connectionRepository.findByReceiverAndStatus(user, "PENDING");
    }

    private UserDTO mapUserToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .department(user.getDepartment())
                .level(user.getLevel())
                .build();
    }
}
