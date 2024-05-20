package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.vote.VoteRequestDTO;
import kit.project.whatshouldweeattoday.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/{voteId}")
    public VoteRequestDTO incrementVote(@PathVariable Long voteId) throws BadRequestException {
        return voteService.incrementVoteCount(voteId);
    }
}
