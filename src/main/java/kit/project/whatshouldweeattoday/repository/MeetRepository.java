package kit.project.whatshouldweeattoday.repository;

import kit.project.whatshouldweeattoday.domain.entity.Meet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetRepository extends JpaRepository<Meet, Long> {
    Optional<Meet> findByRoomIdAndMeetMenu(Long roomId, String meetMenu);
}
