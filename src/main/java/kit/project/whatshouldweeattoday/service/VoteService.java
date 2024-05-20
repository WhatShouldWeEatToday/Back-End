package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.vote.VoteRequestDTO;
import kit.project.whatshouldweeattoday.domain.entity.Vote;
import kit.project.whatshouldweeattoday.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;

    @Transactional
    public VoteRequestDTO incrementVoteCount(Long voteId) throws BadRequestException {
        Vote vote = voteRepository.findById(voteId).orElseThrow(() -> new BadRequestException("존재하지 않는 메뉴 투표입니다."));
        vote.incrementVoteCount();
        Vote savedVote = voteRepository.save(vote);

        return VoteRequestDTO.builder()
                .menu(savedVote.getMenu())
                .voteCount(savedVote.getVoteCount())
                .build();
    }
}
