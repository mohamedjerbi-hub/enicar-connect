package tn.enicar.enicarconnect.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enicar.enicarconnect.dto.ConnectionRequestDTO;
import tn.enicar.enicarconnect.dto.UserDTO;
import tn.enicar.enicarconnect.model.ConnectionRequest;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.ConnectionRequestRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectionService {

    private final ConnectionRequestRepository connectionRepository;
    private final UserRepository userRepository;

    @Transactional
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
        log.info("Connection request sent: senderId={} receiverId={}", senderId, receiverId);
    }

    @Transactional
    public void acceptRequest(Long requestId, Long currentUserId) {
        ConnectionRequest request = connectionRepository.findById(requestId).orElseThrow();

        if (!request.getReceiver().getId().equals(currentUserId)) {
            throw new RuntimeException("Action non autorisée.");
        }

        request.setStatus("ACCEPTED");
        connectionRepository.save(request);
        log.info("Connection request accepted: requestId={} by userId={}", requestId, currentUserId);
    }

    @Transactional
    public void rejectRequest(Long requestId, Long currentUserId) {
        ConnectionRequest request = connectionRepository.findById(requestId).orElseThrow();

        if (!request.getReceiver().getId().equals(currentUserId)) {
            throw new RuntimeException("Action non autorisée.");
        }

        request.setStatus("REJECTED");
        connectionRepository.save(request);
        log.info("Connection request rejected: requestId={} by userId={}", requestId, currentUserId);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getMyNetwork(Long currentUserId) {
        User user = userRepository.findById(currentUserId).orElseThrow();
        return connectionRepository.findAcceptedConnections(user).stream()
                .map(this::mapUserToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConnectionRequestDTO> getPendingRequests(Long currentUserId) {
        User user = userRepository.findById(currentUserId).orElseThrow();
        return connectionRepository.findByReceiverAndStatus(user, "PENDING").stream()
                .map(this::mapConnectionRequestToDTO)
                .collect(Collectors.toList());
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

    private ConnectionRequestDTO mapConnectionRequestToDTO(ConnectionRequest request) {
        return ConnectionRequestDTO.builder()
                .id(request.getId())
                .sender(mapUserToDTO(request.getSender()))
                .status(request.getStatus())
                .timestamp(request.getTimestamp())
                .build();
    }
}
