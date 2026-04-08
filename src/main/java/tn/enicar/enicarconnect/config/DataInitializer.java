package tn.enicar.enicarconnect.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.enicar.enicarconnect.model.*;
import tn.enicar.enicarconnect.repository.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private static final String DEMO_ADMIN_EMAIL = "admin@enicar.ucar.tn";
    private static final String DEMO_ADMIN_PASSWORD = "admin123";

    private final UserRepository userRepo;
    private final GroupRepository groupRepo;
    private final GroupMemberRepository memberRepo;
    private final PostRepository postRepo;
    private final CommentRepository commentRepo;
    private final PostLikeRepository likeRepo;
    private final EventRepository eventRepo;
    private final JobOfferRepository jobRepo;
    private final ResourceFileRepository resourceRepo;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        if (userRepo.count() > 0) {
            ensureDemoAdminCredentials();
            log.info("Database already seeded. Skipping initialization.");
            return;
        }

        log.info("Starting database initialization from seed-data.json...");

        InputStream inputStream = TypeReference.class.getResourceAsStream("/seed-data.json");
        Map<String, List<Map<String, Object>>> data = objectMapper.readValue(inputStream, new TypeReference<>() {});

        // Lookup maps for O(1) access by seed ID
        Map<Integer, User> userMap = new HashMap<>();
        Map<Integer, AppGroup> groupMap = new HashMap<>();
        Map<Integer, Post> postMap = new HashMap<>();

        // 1. Seed Users
        for (Map<String, Object> uMap : data.get("users")) {
            User user = User.builder()
                    .firstName((String) uMap.get("firstName"))
                    .lastName((String) uMap.get("lastName"))
                    .email((String) uMap.get("email"))
                    .password(passwordEncoder.encode((String) uMap.get("password")))
                    .role(Role.valueOf((String) uMap.get("role")))
                    .department((String) uMap.get("department"))
                    .level((String) uMap.get("level"))
                    .avatarColor((String) uMap.get("avatarColor"))
                    .avatarBg((String) uMap.get("avatarBg"))
                    .build();
            user = userRepo.save(user);
            userMap.put((Integer) uMap.get("id"), user);
            log.info("Seeded user: {}", user.getEmail());
        }

        // 2. Seed Groups
        for (Map<String, Object> gMap : data.get("groups")) {
            User creator = userMap.get((Integer) gMap.get("creatorId"));
            AppGroup group = AppGroup.builder()
                    .name((String) gMap.get("name"))
                    .description((String) gMap.get("description"))
                    .groupType(GroupType.valueOf((String) gMap.get("groupType")))
                    .privacy(GroupPrivacy.valueOf((String) gMap.get("privacy")))
                    .icon((String) gMap.get("icon"))
                    .iconColor((String) gMap.get("iconColor"))
                    .bannerGradient((String) gMap.get("bannerGradient"))
                    .creator(creator)
                    .build();
            group = groupRepo.save(group);
            groupMap.put((Integer) gMap.get("id"), group);
            memberRepo.save(GroupMember.builder().group(group).user(creator).memberRole(MemberRole.ADMIN).build());
            log.info("Seeded group: {}", group.getName());
        }

        // 3. Seed Posts
        for (Map<String, Object> pMap : data.get("posts")) {
            User author = userMap.get((Integer) pMap.get("authorId"));
            Integer gId = (Integer) pMap.get("groupId");
            Post post = Post.builder()
                    .author(author)
                    .body((String) pMap.get("body"))
                    .visibility(Visibility.valueOf((String) pMap.get("visibility")))
                    .group(gId != null ? groupMap.get(gId) : null)
                    .build();
            post.setHashtags(extractHashtags(post.getBody()));
            post = postRepo.save(post);
            postMap.put((Integer) pMap.get("id"), post);
        }

        // 4. Seed Comments
        for (Map<String, Object> cMap : data.get("comments")) {
            commentRepo.save(Comment.builder()
                    .post(postMap.get((Integer) cMap.get("postId")))
                    .author(userMap.get((Integer) cMap.get("authorId")))
                    .text((String) cMap.get("text"))
                    .build());
        }

        // 5. Seed Likes
        for (Map<String, Object> lMap : data.get("likes")) {
            likeRepo.save(PostLike.builder()
                    .post(postMap.get((Integer) lMap.get("postId")))
                    .user(userMap.get((Integer) lMap.get("userId")))
                    .build());
        }

        // 6. Seed Events
        for (Map<String, Object> eMap : data.get("events")) {
            User owner = userMap.get((Integer) eMap.get("ownerId"));
            AppEvent event = AppEvent.builder()
                    .title((String) eMap.get("title"))
                    .date((String) eMap.get("date"))
                    .time((String) eMap.get("time"))
                    .location((String) eMap.get("location"))
                    .description((String) eMap.get("description"))
                    .category((String) eMap.get("category"))
                    .organizer((String) eMap.get("organizer"))
                    .color((String) eMap.get("color"))
                    .maxCapacity((Integer) eMap.get("maxCapacity"))
                    .owner(owner)
                    .build();
            eventRepo.save(event);
            log.info("Seeded event: {}", event.getTitle());
        }

        // 7. Seed Job Offers
        for (Map<String, Object> jMap : data.get("jobs")) {
            User author = userMap.get((Integer) jMap.get("authorId"));
            JobOffer job = JobOffer.builder()
                    .title((String) jMap.get("title"))
                    .company((String) jMap.get("company"))
                    .location((String) jMap.get("location"))
                    .type((String) jMap.get("type"))
                    .description((String) jMap.get("description"))
                    .tags((String) jMap.get("tags"))
                    .author(author)
                    .build();
            jobRepo.save(job);
            log.info("Seeded job: {}", job.getTitle());
        }

        // 8. Seed Resources
        for (Map<String, Object> rMap : data.get("resources")) {
            User author = userMap.get((Integer) rMap.get("authorId"));
            ResourceFile resource = ResourceFile.builder()
                    .title((String) rMap.get("title"))
                    .category((String) rMap.get("category"))
                    .fileSize((String) rMap.get("fileSize"))
                    .filePath((String) rMap.get("filePath"))
                    .icon((String) rMap.get("icon"))
                    .author(author)
                    .build();
            resourceRepo.save(resource);
            log.info("Seeded resource: {}", resource.getTitle());
        }

        log.info("Database initialization complete.");
        ensureDemoAdminCredentials();
    }

    private void ensureDemoAdminCredentials() {
        userRepo.findByEmail(DEMO_ADMIN_EMAIL).ifPresent(admin -> {
            if (!passwordEncoder.matches(DEMO_ADMIN_PASSWORD, admin.getPassword())) {
                admin.setPassword(passwordEncoder.encode(DEMO_ADMIN_PASSWORD));
                userRepo.save(admin);
                log.info("Reset demo admin password for {}", DEMO_ADMIN_EMAIL);
            }
        });
    }

    private String extractHashtags(String body) {
        if (body == null) return null;
        java.util.LinkedHashSet<String> tags = new java.util.LinkedHashSet<>();
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("#(\\w+)").matcher(body);
        while (m.find()) tags.add(m.group(1).toLowerCase());
        return tags.isEmpty() ? null : String.join(",", tags);
    }
}
