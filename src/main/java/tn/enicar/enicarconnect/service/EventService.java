package tn.enicar.enicarconnect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.enicar.enicarconnect.dto.AppEventDTO;
import tn.enicar.enicarconnect.model.AppEvent;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.EventRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public List<AppEventDTO> getAllEvents(Long currentUserId) {
        return eventRepository.findAllByOrderByDateAscTimeAsc().stream()
                .map(event -> mapToDTO(event, currentUserId))
                .collect(Collectors.toList());
    }

    public AppEventDTO createEvent(AppEvent eventData, Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        eventData.setOwner(currentUser);
        AppEvent saved = eventRepository.save(eventData);
        return mapToDTO(saved, currentUserId);
    }

    public AppEventDTO updateEvent(Long id, AppEvent updatedData, Long currentUserId) {
        AppEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Optionnel : vérifier si l'utilisateur est le propriétaire (isOwner)
        if (event.getOwner() != null && !event.getOwner().getId().equals(currentUserId)) {
            throw new RuntimeException("Unauthorized");
        }

        event.setTitle(updatedData.getTitle());
        event.setDate(updatedData.getDate());
        event.setTime(updatedData.getTime());
        event.setLocation(updatedData.getLocation());
        event.setDescription(updatedData.getDescription());
        event.setCategory(updatedData.getCategory());
        event.setOrganizer(updatedData.getOrganizer());
        event.setColor(updatedData.getColor());
        event.setMaxCapacity(updatedData.getMaxCapacity());

        AppEvent saved = eventRepository.save(event);
        return mapToDTO(saved, currentUserId);
    }

    public void deleteEvent(Long id, Long currentUserId) {
        AppEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (event.getOwner() != null && !event.getOwner().getId().equals(currentUserId)) {
            throw new RuntimeException("Unauthorized");
        }

        eventRepository.delete(event);
    }

    public AppEventDTO toggleRegister(Long id, Long currentUserId) {
        AppEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (event.getRegisteredUsers().contains(currentUser)) {
            event.getRegisteredUsers().remove(currentUser);
        } else {
            // Check capacity
            if (event.getMaxCapacity() == null || event.getRegisteredUsers().size() < event.getMaxCapacity()) {
                event.getRegisteredUsers().add(currentUser);
            } else {
                throw new RuntimeException("Événement complet");
            }
        }

        AppEvent saved = eventRepository.save(event);
        return mapToDTO(saved, currentUserId);
    }

    private AppEventDTO mapToDTO(AppEvent event, Long currentUserId) {
        boolean isRegistered = event.getRegisteredUsers().stream()
                .anyMatch(u -> u.getId().equals(currentUserId));
        boolean isOwner = event.getOwner() != null && event.getOwner().getId().equals(currentUserId);

        return AppEventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .date(event.getDate())
                .time(event.getTime())
                .location(event.getLocation())
                .description(event.getDescription())
                .category(event.getCategory())
                .organizer(event.getOrganizer())
                .color(event.getColor())
                .maxCapacity(event.getMaxCapacity())
                .registeredCount(event.getRegisteredUsers().size())
                .registered(isRegistered)
                .isOwner(isOwner)
                .build();
    }
}
