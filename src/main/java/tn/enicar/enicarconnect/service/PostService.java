package tn.enicar.enicarconnect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enicar.enicarconnect.dto.*;
import tn.enicar.enicarconnect.model.*;
import tn.enicar.enicarconnect.repository.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepo;
    private final CommentRepository commentRepo;
    private final PostLikeRepository likeRepo;
    private final PostReportRepository reportRepo;
    private final UserRepository userRepo;
    private final GroupRepository groupRepo;

    // ─── CREATE ───────────────────────────────────────────

    @Transactional
    public PostDTO createPost(Long userId, CreatePostRequest req) {
        User author = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Post post = Post.builder()
                .author(author)
                .body(req.getBody())
                .visibility(Visibility.valueOf(req.getVisibility()))
                .hashtags(extractHashtags(req.getBody(), req.getHashtags()))
                .mediaUrls(req.getMediaUrls() != null ? String.join(",", req.getMediaUrls()) : null)
                .build();

        if (req.getGroupId() != null) {
            post.setGroup(groupRepo.getReferenceById(req.getGroupId()));
            // Group posts are usually reserved for group members
        }

        // Handle mentions
        if (req.getMentionedUserIds() != null && !req.getMentionedUserIds().isEmpty()) {
            List<User> mentioned = userRepo.findAllById(req.getMentionedUserIds());
            post.setMentions(mentioned);
        }

        post = postRepo.save(post);
        return toDTO(post, userId);
    }

    // ─── READ ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PostDTO> getAllPosts(Long currentUserId) {
        return postRepo.findAllPublicPosts().stream()
                .map(p -> toDTO(p, currentUserId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostDTO getPost(Long postId, Long currentUserId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Publication introuvable"));
        return toDTO(post, currentUserId);
    }

    // ─── UPDATE ───────────────────────────────────────────

    @Transactional
    public PostDTO updatePost(Long postId, Long userId, CreatePostRequest req) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Publication introuvable"));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Vous ne pouvez modifier que vos propres publications");
        }

        post.setBody(req.getBody());
        post.setVisibility(Visibility.valueOf(req.getVisibility()));
        post.setHashtags(extractHashtags(req.getBody(), req.getHashtags()));

        if (req.getMediaUrls() != null) {
            post.setMediaUrls(String.join(",", req.getMediaUrls()));
        }

        post = postRepo.save(post);
        return toDTO(post, userId);
    }

    // ─── DELETE ───────────────────────────────────────────

    @Transactional
    public void deletePost(Long postId, Long userId, boolean isAdmin) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Publication introuvable"));

        if (!post.getAuthor().getId().equals(userId) && !isAdmin) {
            throw new RuntimeException("Vous ne pouvez supprimer que vos propres publications");
        }

        postRepo.delete(post);
    }

    // ─── LIKE / UNLIKE ───────────────────────────────────

    @Transactional
    public PostDTO toggleLike(Long postId, Long userId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Publication introuvable"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Optional<PostLike> existing = likeRepo.findByPostAndUser(post, user);
        if (existing.isPresent()) {
            likeRepo.delete(existing.get());
        } else {
            likeRepo.save(PostLike.builder().post(post).user(user).build());
        }

        return toDTO(postRepo.findById(postId).get(), userId);
    }

    // ─── COMMENTS ─────────────────────────────────────────

    @Transactional
    public CommentDTO addComment(Long postId, Long userId, String text) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Publication introuvable"));
        User author = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Comment comment = Comment.builder()
                .post(post)
                .author(author)
                .text(text)
                .build();

        comment = commentRepo.save(comment);
        return toCommentDTO(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId, boolean isAdmin) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Commentaire introuvable"));

        if (!comment.getAuthor().getId().equals(userId) && !isAdmin) {
            throw new RuntimeException("Vous ne pouvez supprimer que vos propres commentaires");
        }

        commentRepo.delete(comment);
    }

    // ─── REPORTING ────────────────────────────────────────

    @Transactional
    public void reportPost(Long postId, Long userId, String reason) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Publication introuvable"));
        User reporter = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        PostReport report = PostReport.builder()
                .post(post)
                .reporter(reporter)
                .reason(reason)
                .build();

        reportRepo.save(report);
    }

    @Transactional(readOnly = true)
    public List<PostDTO> getReportedPosts(Long currentUserId) {
        return postRepo.findReportedPosts().stream()
                .map(p -> toDTO(p, currentUserId))
                .collect(Collectors.toList());
    }

    @Transactional
    public PostDTO moderatePost(Long postId, String action, Long currentUserId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Publication introuvable"));

        if ("REMOVE".equalsIgnoreCase(action)) {
            post.setModerated(true);
            post.getReports().forEach(r -> r.setStatus(ReportStatus.REVIEWED));
        } else if ("DISMISS".equalsIgnoreCase(action)) {
            post.getReports().forEach(r -> r.setStatus(ReportStatus.DISMISSED));
        }

        post = postRepo.save(post);
        return toDTO(post, currentUserId);
    }

    // ─── MAPPING ──────────────────────────────────────────

    private PostDTO toDTO(Post post, Long currentUserId) {
        User author = post.getAuthor();
        User currentUser = currentUserId != null ? userRepo.findById(currentUserId).orElse(null) : null;

        boolean liked = currentUser != null && likeRepo.existsByPostAndUser(post, currentUser);

        List<CommentDTO> commentDTOs = post.getComments().stream()
                .map(this::toCommentDTO)
                .collect(Collectors.toList());

        List<PostDTO.MentionDTO> mentionDTOs = post.getMentions().stream()
                .map(u -> PostDTO.MentionDTO.builder()
                        .id(u.getId())
                        .name(u.getFullName())
                        .initials(u.getInitials())
                        .build())
                .collect(Collectors.toList());

        List<String> mediaList = post.getMediaUrls() != null && !post.getMediaUrls().isEmpty()
                ? Arrays.asList(post.getMediaUrls().split(","))
                : Collections.emptyList();

        List<String> hashtagList = post.getHashtags() != null && !post.getHashtags().isEmpty()
                ? Arrays.asList(post.getHashtags().split(","))
                : Collections.emptyList();

        return PostDTO.builder()
                .id(post.getId())
                .authorId(author.getId())
                .authorName(author.getFullName())
                .authorInitials(author.getInitials())
                .authorRole(author.getRole().name().toLowerCase())
                .authorAvatarColor(author.getAvatarColor())
                .authorAvatarBg(author.getAvatarBg())
                .groupId(post.getGroup() != null ? post.getGroup().getId() : null)
                .body(post.getBody())
                .visibility(post.getVisibility().name())
                .hashtags(hashtagList)
                .mediaUrls(mediaList)
                .mentions(mentionDTOs)
                .likesCount(post.getLikes().size())
                .likedByMe(liked)
                .commentsCount(post.getComments().size())
                .comments(commentDTOs)
                .reportsCount(post.getReports().size())
                .moderated(post.isModerated())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    private CommentDTO toCommentDTO(Comment c) {
        User a = c.getAuthor();
        return CommentDTO.builder()
                .id(c.getId())
                .authorId(a.getId())
                .authorName(a.getFullName())
                .authorInitials(a.getInitials())
                .authorRole(a.getRole().name().toLowerCase())
                .authorAvatarColor(a.getAvatarColor())
                .authorAvatarBg(a.getAvatarBg())
                .text(c.getText())
                .createdAt(c.getCreatedAt())
                .build();
    }

    private String extractHashtags(String body, List<String> explicit) {
        Set<String> tags = new LinkedHashSet<>();

        // Extract from body text
        if (body != null) {
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("#(\\w+)").matcher(body);
            while (m.find()) {
                tags.add(m.group(1).toLowerCase());
            }
        }

        // Add explicit hashtags
        if (explicit != null) {
            explicit.forEach(t -> tags.add(t.toLowerCase().replace("#", "")));
        }

        return tags.isEmpty() ? null : String.join(",", tags);
    }
}
