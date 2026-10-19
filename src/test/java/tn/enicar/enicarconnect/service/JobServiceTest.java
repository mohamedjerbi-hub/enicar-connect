package tn.enicar.enicarconnect.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.enicar.enicarconnect.model.JobApplication;
import tn.enicar.enicarconnect.model.JobOffer;
import tn.enicar.enicarconnect.model.User;
import tn.enicar.enicarconnect.repository.JobApplicationRepository;
import tn.enicar.enicarconnect.repository.JobOfferRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {
    @Mock JobOfferRepository jobOffers;
    @Mock JobApplicationRepository applications;
    @Mock UserRepository users;
    @InjectMocks JobService service;
    User owner;
    JobOffer job;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setFirstName("Owner");
        owner.setLastName("User");
        job = JobOffer.builder().id(10L).title("Dev").company("ACME").location("Tunis")
                .type("PFE").description("Backend").tags("java,spring")
                .requiredSkills(new java.util.HashSet<>(Set.of("Java", "Spring")))
                .author(owner).createdAt(LocalDateTime.now().minusHours(2)).build();
    }

    @Test
    void readsAndMapsJobs() {
        when(users.findById(1L)).thenReturn(Optional.of(owner));
        when(jobOffers.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(job));
        when(applications.existsByJobOfferAndApplicant(job, owner)).thenReturn(true);

        var result = service.getAllJobs(1L);

        assertEquals(1, result.size());
        assertTrue(result.get(0).isApplied());
        assertTrue(result.get(0).isOwner());
        assertEquals(List.of("java", "spring"), result.get(0).getTags());
    }

    @Test
    void createsAndUpdatesOwnedJob() {
        when(users.findById(1L)).thenReturn(Optional.of(owner));
        when(jobOffers.save(job)).thenReturn(job);
        assertEquals("Dev", service.createJob(job, 1L).getTitle());

        JobOffer update = JobOffer.builder().title("Senior Dev").company("New")
                .location("Sousse").type("CDI").description("API").tags("java")
                .requiredSkills(new java.util.HashSet<>(Set.of("Java"))).build();
        when(jobOffers.findById(10L)).thenReturn(Optional.of(job));
        assertEquals("Senior Dev", service.updateJob(10L, update, 1L).getTitle());
        verify(jobOffers, times(2)).save(job);
    }

    @Test
    void rejectsUpdateAndDeleteByOtherUser() {
        User other = new User();
        other.setId(2L);
        when(jobOffers.findById(10L)).thenReturn(Optional.of(job));
        when(users.findById(2L)).thenReturn(Optional.of(other));

        assertThrows(RuntimeException.class, () -> service.updateJob(10L, job, 2L));
        assertThrows(RuntimeException.class, () -> service.deleteJob(10L, 2L));
    }

    @Test
    void deletesOwnedJob() {
        when(jobOffers.findById(10L)).thenReturn(Optional.of(job));
        service.deleteJob(10L, 1L);
        verify(jobOffers).delete(job);
    }

    @Test
    void appliesOnceAndIgnoresDuplicate() {
        User applicant = new User();
        applicant.setId(2L);
        when(jobOffers.findById(10L)).thenReturn(Optional.of(job));
        when(users.findById(2L)).thenReturn(Optional.of(applicant));
        when(applications.existsByJobOfferAndApplicant(job, applicant)).thenReturn(false, true);

        service.applyToJob(10L, 2L);
        service.applyToJob(10L, 2L);
        verify(applications).save(any(JobApplication.class));
    }

    @Test
    void rejectsOwnApplicationAndMissingUsers() {
        when(jobOffers.findById(10L)).thenReturn(Optional.of(job));
        assertThrows(RuntimeException.class, () -> service.applyToJob(10L, 1L));
        when(users.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getAllJobs(99L));
    }

    @Test
    void mapsRelativeTimeBranchesAndEmptyTags() {
        when(users.findById(1L)).thenReturn(Optional.of(owner));
        when(applications.existsByJobOfferAndApplicant(any(), any())).thenReturn(false);
        for (LocalDateTime date : java.util.Arrays.asList(null, LocalDateTime.now(), LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(14))) {
            JobOffer item = JobOffer.builder().id(11L).author(owner).tags("").createdAt(date).build();
            when(jobOffers.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(item));
            assertNotNull(service.getAllJobs(1L).get(0).getPosted());
        }
    }
}
