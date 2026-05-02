package tn.enicar.enicarconnect.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enicar.enicarconnect.dto.MessageDTO;
import tn.enicar.enicarconnect.model.AppGroup;
import tn.enicar.enicarconnect.model.Message;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.GroupMemberRepository;
import tn.enicar.enicarconnect.repository.GroupRepository;
import tn.enicar.enicarconnect.repository.MessageRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public MessageDTO sendMessage(Long senderId, Long groupId, String content) {
        if (content == null || content.isBlank()) {
            throw new RuntimeException("Le contenu du message est requis");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        AppGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));

        boolean isMember = groupMemberRepository.existsByGroupAndUser(group, sender);
        if (!isMember) {
            log.warn("Group message rejected: userId={} groupId={} reason=not_member", senderId, groupId);
            throw new RuntimeException("Vous devez être membre du groupe pour envoyer un message");
        }

        Message saved = messageRepository.save(Message.builder()
                .content(content.trim())
                .sender(sender)
                .recipientGroup(group)
                .build());

        log.info("Group message sent: messageId={} groupId={} senderId={}", saved.getId(), groupId, senderId);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getGroupMessages(Long requesterId, Long groupId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        AppGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));

        boolean isMember = groupMemberRepository.existsByGroupAndUser(group, requester);
        if (!isMember) {
            log.warn("Group messages access denied: userId={} groupId={} reason=not_member", requesterId, groupId);
            throw new RuntimeException("Accès refusé : vous n'êtes pas membre du groupe");
        }

        return messageRepository.findByRecipientGroupIdOrderByTimestampAsc(groupId).stream()
                .map(this::toDTO)
                .toList();
    }

    private MessageDTO toDTO(Message message) {
        User sender = message.getSender();
        AppGroup group = message.getRecipientGroup();

        return MessageDTO.builder()
                .id(message.getId())
                .groupId(group != null ? group.getId() : null)
                .senderId(sender != null ? sender.getId() : null)
                .senderName(sender != null ? sender.getFullName() : null)
                .senderInitials(sender != null ? sender.getInitials() : null)
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .build();
    }
}

