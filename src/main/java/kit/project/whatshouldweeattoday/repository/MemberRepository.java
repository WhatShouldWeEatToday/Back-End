package kit.project.whatshouldweeattoday.repository;

import kit.project.whatshouldweeattoday.domain.dto.friend.FriendListResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByLoginId(String loginId);
    boolean existsByNickname(String nickname);
    Optional<Member> findByLoginId(String loginId);
    Set<Member> findAllByLoginIdIn(List<String> loginId);
    Page<FriendListResponseDTO> findByLoginIdContaining(String loginId, Pageable pageable);}
