package kit.project.whatshouldweeattoday.domain.dto.meet;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DepartureResponseDTO {

    private Integer memberCount;
    private String meetMenu;
    private List<String> departureList;

    public DepartureResponseDTO(Integer memberCount, String meetMenu, List<String> departureList) {
        this.memberCount = memberCount;
        this.meetMenu = meetMenu;
        this.departureList = departureList;
    }
}
