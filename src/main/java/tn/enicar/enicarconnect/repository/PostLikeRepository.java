package tn.enicar.enicarconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enicar.enicarconnect.model.Post;
import tn.enicar.enicarconnect.model.PostLike;
import tn.enicar.enicarconnect.model.User;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPostAndUser(Post post, User user);

    Optional<PostLike> findByPostAndUser(Post post, User user);

    int countByPost(Post post);
}
