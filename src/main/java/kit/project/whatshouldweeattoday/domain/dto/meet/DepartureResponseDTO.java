package kit.project.whatshouldweeattoday.domain.dto.meet;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DepartureResponseDTO {

    private String meetMenu;
    private List<String> departureList;

    public DepartureResponseDTO(String meetMenu, List<String> departureList) {
        this.meetMenu = meetMenu;
        this.departureList = departureList;
    }
}
