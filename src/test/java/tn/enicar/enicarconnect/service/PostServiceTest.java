package tn.enicar.enicarconnect.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.enicar.enicarconnect.dto.CommentDTO;
import tn.enicar.enicarconnect.dto.CreatePostRequest;
import tn.enicar.enicarconnect.dto.PostDTO;
import tn.enicar.enicarconnect.model.*;
import tn.enicar.enicarconnect.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService Unit Tests (Senior QA)")
class PostServiceTest {

    @Mock
    private PostRepository postRepo;

    @Mock
    private CommentRepository commentRepo;

    @Mock
    private PostLikeRepository likeRepo;

    @Mock
    private PostReportRepository reportRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private GroupRepository groupRepo;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("TC-POST-01: Should create public post with hashtags and return PostDTO")
    void shouldCreatePostSuccessfully() {
        // Arrange
        User author = User.builder().id(10L).firstName("Mohamed").lastName("Babou").role(Role.STUDENT).build();
        when(userRepo.findById(10L)).thenReturn(Optional.of(author));

        CreatePostRequest req = new CreatePostRequest();
        req.setBody("Exciting news #spring #testing at ENICAR!");
        req.setVisibility("PUBLIC");

        when(postRepo.save(any(Post.class))).thenAnswer(i -> {
            Post p = i.getArgument(0);
            p.setId(100L);
            p.setCreatedAt(LocalDateTime.now());
            p.setLikes(new HashSet<>());
            p.setComments(new ArrayList<>());
            return p;
        });

        // Act
        PostDTO dto = postService.createPost(10L, req);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getBody()).contains("#spring");
        assertThat(dto.getHashtags()).contains("spring", "testing");
        verify(postRepo).save(any(Post.class));
    }

    @Test
    @DisplayName("TC-POST-02: Should toggle like - add like when not previously liked")
    void shouldAddLikeWhenNotLiked() {
        // Arrange
        User author = User.builder().id(1L).role(Role.STUDENT).build();
        User liker = User.builder().id(2L).role(Role.STUDENT).build();
        Post post = Post.builder().id(50L).author(author).likes(new HashSet<>()).comments(new ArrayList<>()).build();

        when(postRepo.findById(50L)).thenReturn(Optional.of(post));
        when(userRepo.findById(2L)).thenReturn(Optional.of(liker));
        when(likeRepo.findByPostAndUser(post, liker)).thenReturn(Optional.empty());

        // Act
        PostDTO dto = postService.toggleLike(50L, 2L);

        // Assert
        assertThat(dto).isNotNull();
        verify(likeRepo).save(any(PostLike.class));
        verify(likeRepo, never()).delete(any(PostLike.class));
    }

    @Test
    @DisplayName("TC-POST-03: Should toggle like - remove like when already liked")
    void shouldRemoveLikeWhenAlreadyLiked() {
        // Arrange
        User author = User.builder().id(1L).role(Role.STUDENT).build();
        User liker = User.builder().id(2L).role(Role.STUDENT).build();
        PostLike existingLike = PostLike.builder().id(300L).build();
        Set<PostLike> likes = new HashSet<>();
        likes.add(existingLike);

        Post post = Post.builder().id(50L).author(author).likes(likes).comments(new ArrayList<>()).build();

        when(postRepo.findById(50L)).thenReturn(Optional.of(post));
        when(userRepo.findById(2L)).thenReturn(Optional.of(liker));
        when(likeRepo.findByPostAndUser(post, liker)).thenReturn(Optional.of(existingLike));

        // Act
        PostDTO dto = postService.toggleLike(50L, 2L);

        // Assert
        assertThat(dto).isNotNull();
        verify(likeRepo).delete(existingLike);
    }

    @Test
    @DisplayName("TC-POST-04: Should add comment to post successfully")
    void shouldAddCommentSuccessfully() {
        // Arrange
        User commenter = User.builder().id(5L).firstName("Oussema").lastName("Mejri").role(Role.STUDENT).build();
        Post post = Post.builder().id(77L).build();

        when(postRepo.findById(77L)).thenReturn(Optional.of(post));
        when(userRepo.findById(5L)).thenReturn(Optional.of(commenter));
        when(commentRepo.save(any(Comment.class))).thenAnswer(i -> {
            Comment c = i.getArgument(0);
            c.setId(101L);
            c.setCreatedAt(LocalDateTime.now());
            return c;
        });

        // Act
        CommentDTO commentDTO = postService.addComment(77L, 5L, "Excellente initiative !");

        // Assert
        assertThat(commentDTO).isNotNull();
        assertThat(commentDTO.getText()).isEqualTo("Excellente initiative !");
        assertThat(commentDTO.getAuthorName()).isEqualTo("Oussema Mejri");
        verify(commentRepo).save(any(Comment.class));
    }

    @Test
    @DisplayName("TC-POST-05: Non-author cannot delete post unless admin")
    void shouldRejectDeleteByNonAuthorNonAdmin() {
        // Arrange
        User author = User.builder().id(1L).build();
        Post post = Post.builder().id(99L).author(author).build();
        when(postRepo.findById(99L)).thenReturn(Optional.of(post));

        // Act & Assert
        assertThatThrownBy(() -> postService.deletePost(99L, 2L, false))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Vous ne pouvez supprimer que vos propres publications");

        verify(postRepo, never()).delete(any());
    }

    @Test
    @DisplayName("TC-POST-06: Admin can delete any post")
    void shouldAllowAdminToDeleteAnyPost() {
        // Arrange
        User author = User.builder().id(1L).build();
        Post post = Post.builder().id(99L).author(author).build();
        when(postRepo.findById(99L)).thenReturn(Optional.of(post));

        // Act
        postService.deletePost(99L, 2L, true);

        // Assert
        verify(postRepo).delete(post);
    }
}
