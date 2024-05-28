package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.entity.Meet;
import kit.project.whatshouldweeattoday.repository.MeetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetService {

    private final MeetRepository meetRepository;
    private final VoteService voteService;

    public void finalizeMeet(Long meetId) {
        Meet meet = meetRepository.findById(meetId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid meet ID"));

        String mostVotedMenu = voteService.getMostVotedMenu();
//        meet.updateMeet(meet.getMeetLocate(), mostVotedMenu, meet.getMeetTime());
        meetRepository.save(meet);
    }
}
