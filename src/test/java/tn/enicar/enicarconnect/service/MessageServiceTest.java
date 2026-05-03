package tn.enicar.enicarconnect.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.enicar.enicarconnect.dto.MessageDTO;
import tn.enicar.enicarconnect.model.AppGroup;
import tn.enicar.enicarconnect.model.Message;
import tn.enicar.enicarconnect.model.Role;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.GroupMemberRepository;
import tn.enicar.enicarconnect.repository.GroupRepository;
import tn.enicar.enicarconnect.repository.MessageRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageService messageService;

    @Test
    void shouldSendMessageWhenUserIsGroupMember() {
        User sender = baseUser(10L, "a@x.tn");
        AppGroup group = AppGroup.builder().id(5L).name("G").build();

        when(userRepository.findById(10L)).thenReturn(Optional.of(sender));
        when(groupRepository.findById(5L)).thenReturn(Optional.of(group));
        when(groupMemberRepository.existsByGroupAndUser(group, sender)).thenReturn(true);

        Message persisted = Message.builder()
                .id(99L)
                .content("Hello groupe")
                .sender(sender)
                .recipientGroup(group)
                .timestamp(LocalDateTime.now())
                .build();
        when(messageRepository.save(any(Message.class))).thenReturn(persisted);

        MessageDTO dto = messageService.sendMessage(10L, 5L, " Hello groupe ");

        assertThat(dto.getContent()).isEqualTo("Hello groupe");
        assertThat(dto.getGroupId()).isEqualTo(5L);
        assertThat(dto.getSenderId()).isEqualTo(10L);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void shouldRejectBlankMessage() {
        assertThatThrownBy(() -> messageService.sendMessage(1L, 1L, "   "))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("contenu");
    }

    @Test
    void shouldRejectSendWhenNotMember() {
        User sender = baseUser(1L, "s@x.tn");
        AppGroup group = AppGroup.builder().id(2L).name("Privé").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(groupRepository.findById(2L)).thenReturn(Optional.of(group));
        when(groupMemberRepository.existsByGroupAndUser(group, sender)).thenReturn(false);

        assertThatThrownBy(() -> messageService.sendMessage(1L, 2L, "Salut"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("membre du groupe");

        verifyNoInteractions(messageRepository);
    }

    @Test
    void shouldListMessagesForMember() {
        User requester = baseUser(3L, "r@x.tn");
        AppGroup group = AppGroup.builder().id(7L).name("Classe").build();

        Message m = Message.builder()
                .id(1L)
                .content("msg")
                .sender(requester)
                .recipientGroup(group)
                .timestamp(LocalDateTime.now())
                .build();

        when(userRepository.findById(3L)).thenReturn(Optional.of(requester));
        when(groupRepository.findById(7L)).thenReturn(Optional.of(group));
        when(groupMemberRepository.existsByGroupAndUser(group, requester)).thenReturn(true);
        when(messageRepository.findByRecipientGroupIdOrderByTimestampAsc(7L)).thenReturn(List.of(m));

        List<MessageDTO> dtos = messageService.getGroupMessages(3L, 7L);

        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).getContent()).isEqualTo("msg");
    }

    private static User baseUser(long id, String email) {
        return User.builder()
                .id(id)
                .email(email)
                .firstName("Fn")
                .lastName("Ln")
                .role(Role.STUDENT)
                .build();
    }
}
