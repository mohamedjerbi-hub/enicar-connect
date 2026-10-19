package tn.enicar.enicarconnect.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.enicar.enicarconnect.model.DocumentRequest;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.DocumentRequestRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {
    @Mock DocumentRequestRepository documents;
    @Mock UserRepository users;
    @InjectMocks DocumentService service;

    @Test
    void createsAndListsRequests() {
        User user = new User();
        user.setId(1L);
        when(users.findById(1L)).thenReturn(Optional.of(user));
        DocumentRequest request = DocumentRequest.builder().requestType(DocumentRequest.RequestType.ATTESTATION)
                .notes("urgent").requestedBy(user).status(DocumentRequest.RequestStatus.PENDING).build();
        when(documents.save(any())).thenReturn(request);
        assertEquals(DocumentRequest.RequestStatus.PENDING,
                service.createRequest(1L, DocumentRequest.RequestType.ATTESTATION, "urgent").getStatus());
        when(documents.findByRequestedBy(user)).thenReturn(List.of(request));
        assertEquals(1, service.getMyRequests(1L).size());
    }

    @Test
    void approvesAndRejectsRequests() {
        DocumentRequest request = DocumentRequest.builder().id(3L).build();
        when(documents.findById(3L)).thenReturn(Optional.of(request));
        when(documents.save(request)).thenReturn(request);
        assertEquals(DocumentRequest.RequestStatus.APPROVED, service.approveRequest(3L).getStatus());
        assertNotNull(request.getProcessedAt());
        assertEquals(DocumentRequest.RequestStatus.REJECTED, service.rejectRequest(3L, "missing").getStatus());
        assertEquals("missing", request.getNotes());
        when(documents.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.approveRequest(99L));
    }
}
