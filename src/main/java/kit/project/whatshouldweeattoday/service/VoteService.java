package kit.project.whatshouldweeattoday.service;

import jakarta.persistence.EntityManager;
import kit.project.whatshouldweeattoday.domain.entity.Vote;
import kit.project.whatshouldweeattoday.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.parser.Entity;

@Service
@RequiredArgsConstructor
@Transactional
public class VoteService {

    private final VoteRepository voteRepository;

    public Vote createVote(String menu1, String menu2) {
        Vote vote = Vote.createVote(menu1, menu2);
        return voteRepository.save(vote);
    }

    public Vote getVote(Long voteId) throws BadRequestException {
        return voteRepository.findById(voteId).orElseThrow(() -> new BadRequestException("존재하지 않는 투표 ID 입니다."));
    }

    public String getMostVotedMenu(Long voteId) throws BadRequestException {
        Vote vote = getVote(voteId);
        if (vote.getVoteCount1() > vote.getVoteCount2()) {
            return vote.getMenu1();
        } else if (vote.getVoteCount1() < vote.getVoteCount2()) {
            return vote.getMenu2();
        }
        else {
            // 투표수가 동일한 경우 처리 (예: 무작위 선택, null 반환 등)
            return "투표수가 동일합니다.";
        }
    }
}