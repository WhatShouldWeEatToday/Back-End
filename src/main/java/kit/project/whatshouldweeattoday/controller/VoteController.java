package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.entity.Vote;
import kit.project.whatshouldweeattoday.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/{menu}")
    public Vote incrementVote(@PathVariable("menu") String menu) {
        return voteService.incrementVote(menu);
    }
}
