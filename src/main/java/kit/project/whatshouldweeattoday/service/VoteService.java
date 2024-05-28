package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.vote.VoteRequestDTO;
import kit.project.whatshouldweeattoday.domain.entity.Vote;
import kit.project.whatshouldweeattoday.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VoteService {

    private final VoteRepository voteRepository;

    public Vote incrementVote(String menu) {
        Vote vote = voteRepository.findByMenu(menu)
                .orElseGet(() -> voteRepository.save(Vote.createVote(menu)));
        vote.incrementVoteCount();
        return voteRepository.save(vote);
    }

    @Transactional(readOnly = true)
    public String getMostVotedMenu() {
        List<Vote> votes = voteRepository.findAll();
        return votes.stream()
                .max(Comparator.comparingLong(Vote::getVoteCount))
                .map(Vote::getMenu)
                .orElse(null);
    }
}
