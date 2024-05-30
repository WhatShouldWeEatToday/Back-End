package kit.project.whatshouldweeattoday.domain.dto.vote;

import lombok.Getter;

@Getter
public class VoteCount {

    private Long menu1Count;
    private Long menu2Count;

    public void incrementMenu1() {
        this.menu1Count++;
    }

    public void incrementMenu2() {
        this.menu2Count++;
    }
}