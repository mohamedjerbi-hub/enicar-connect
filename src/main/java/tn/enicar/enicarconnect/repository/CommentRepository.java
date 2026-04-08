package tn.enicar.enicarconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicar.enicarconnect.model.Comment;
import tn.enicar.enicarconnect.model.Post;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostOrderByCreatedAtAsc(Post post);

    int countByPost(Post post);
}
