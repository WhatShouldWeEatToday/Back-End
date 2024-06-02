package kit.project.whatshouldweeattoday.repository;

import kit.project.whatshouldweeattoday.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT cr FROM ChatRoom cr JOIN FETCH cr.chatRoomMembers crm JOIN FETCH crm.member")
    List<ChatRoom> findAllWithMembers();
}
