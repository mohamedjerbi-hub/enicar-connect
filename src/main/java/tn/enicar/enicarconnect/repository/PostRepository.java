package tn.enicar.enicarconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.enicar.enicarconnect.model.Post;
import tn.enicar.enicarconnect.model.User;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    /** All non-moderated public posts, newest first */
    @EntityGraph(attributePaths = {"author", "group", "mentions", "likes", "comments", "comments.author", "reports"})
    @Query("SELECT p FROM Post p WHERE p.moderated = false AND p.visibility = 'PUBLIC' ORDER BY p.createdAt DESC")
    List<Post> findAllPublicPostsWithDetails();

    /** All posts by a specific author, newest first */
    List<Post> findByAuthorOrderByCreatedAtDesc(User author);

    /** Posts containing a specific hashtag */
    @Query("SELECT p FROM Post p WHERE p.moderated = false AND p.hashtags LIKE %:tag% ORDER BY p.createdAt DESC")
    List<Post> findByHashtag(@Param("tag") String tag);

    /** Reported posts for moderation */
    @EntityGraph(attributePaths = {"author", "group", "mentions", "likes", "comments", "comments.author", "reports"})
    @Query("SELECT DISTINCT p FROM Post p JOIN p.reports r WHERE r.status = 'PENDING' ORDER BY p.createdAt DESC")
    List<Post> findReportedPostsWithDetails();

    @EntityGraph(attributePaths = {"author", "group", "mentions", "likes", "comments", "comments.author", "reports"})
    java.util.Optional<Post> findById(Long id);

    /** All posts in a specific group */
    List<Post> findByGroupIdOrderByCreatedAtDesc(Long groupId);
}
