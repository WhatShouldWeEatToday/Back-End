package kit.project.whatshouldweeattoday.repository;

import kit.project.whatshouldweeattoday.domain.entity.Friendship;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Optional<Member> findOneByMemberLoginIdAndFriendLoginId(String memberLoginId, String friendLoginId);
}
