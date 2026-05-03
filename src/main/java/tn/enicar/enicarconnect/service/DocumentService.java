package tn.enicar.enicarconnect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicar.enicarconnect.model.DocumentRequest;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.DocumentRequestRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRequestRepository documentRepository;
    private final UserRepository userRepository;

    public DocumentRequest createRequest(Long userId, DocumentRequest.RequestType type, String notes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        DocumentRequest request = DocumentRequest.builder()
                .requestType(type)
                .status(DocumentRequest.RequestStatus.PENDING)
                .requestedBy(user)
                .notes(notes)
                .build();
                
        return documentRepository.save(request);
    }

    public List<DocumentRequest> getMyRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return documentRepository.findByRequestedBy(user);
    }

    public DocumentRequest approveRequest(Long id) {
        DocumentRequest request = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(DocumentRequest.RequestStatus.APPROVED);
        request.setProcessedAt(LocalDateTime.now());
        generatePdf(request);
        return documentRepository.save(request);
    }

    public DocumentRequest rejectRequest(Long id, String reason) {
        DocumentRequest request = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(DocumentRequest.RequestStatus.REJECTED);
        request.setProcessedAt(LocalDateTime.now());
        request.setNotes(reason);
        return documentRepository.save(request);
    }

    private void generatePdf(DocumentRequest request) {
        // TODO: Implement PDF generation using iText or equivalent
        System.out.println("Generating PDF for request: " + request.getId());
    }
}
