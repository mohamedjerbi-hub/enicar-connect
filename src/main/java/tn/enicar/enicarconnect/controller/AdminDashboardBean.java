package tn.enicar.enicarconnect.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import tn.enicar.enicarconnect.repository.GroupRepository;
import tn.enicar.enicarconnect.repository.JobOfferRepository;
import tn.enicar.enicarconnect.repository.MentorshipRequestRepository;
import tn.enicar.enicarconnect.repository.PostRepository;
import tn.enicar.enicarconnect.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

/**
 * Tableau de bord JSF admin — indicateurs réels issus de la base PostgreSQL.
 */
@Component("adminDashboardBean")
@RequestScope
@RequiredArgsConstructor
public class AdminDashboardBean {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PostRepository postRepository;
    private final JobOfferRepository jobOfferRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    public String getWelcomeMessage() {
        return "ENICAR Connect — Panneau d'administration (indicateurs en temps réel)";
    }

    public long getUserCount() {
        return userRepository.count();
    }

    public long getGroupCount() {
        return groupRepository.count();
    }

    public long getPostCount() {
        return postRepository.count();
    }

    public long getJobOfferCount() {
        return jobOfferRepository.count();
    }

    public long getMentorshipRequestCount() {
        return mentorshipRequestRepository.count();
    }

    public List<String> getSystemStats() {
        return Arrays.asList(
                "Utilisateurs : " + getUserCount(),
                "Groupes : " + getGroupCount(),
                "Publications : " + getPostCount(),
                "Offres d'emploi : " + getJobOfferCount(),
                "Demandes de mentorat : " + getMentorshipRequestCount(),
                "API : /api/** (JWT) — ce panneau : /admin/dashboard.xhtml"
        );
    }
}
