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

    public Vote createVote(String menu1, String menu2) {
        Vote vote = Vote.createVote(menu1, menu2);
        return voteRepository.save(vote);
    }

    @Transactional
    public void incrementVoteCount1(Long voteId) throws BadRequestException {
        Vote vote = voteRepository.findById(voteId).orElseThrow(() -> new BadRequestException("존재하지 않는 투표 ID 입니다."));
        vote.incrementVoteCount1();
        voteRepository.save(vote);
    }

    @Transactional
    public void incrementVoteCount2(Long voteId) throws BadRequestException {
        Vote vote = voteRepository.findById(voteId).orElseThrow(() -> new BadRequestException("존재하지 않는 투표 ID 입니다."));
        vote.incrementVoteCount1();
        voteRepository.save(vote);
    }

    public Vote getVote(Long voteId) throws BadRequestException {
        return voteRepository.findById(voteId).orElseThrow(() -> new BadRequestException("존재하지 않는 투표 ID 입니다."));
    }
}