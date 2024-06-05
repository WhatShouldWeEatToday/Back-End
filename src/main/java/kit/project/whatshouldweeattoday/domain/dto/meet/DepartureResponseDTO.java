package kit.project.whatshouldweeattoday.domain.dto.meet;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DepartureResponseDTO {

    private Integer memberCount;
    private String meetMenu;
    private String meetDate;
    private List<String> departureList;

    public DepartureResponseDTO(Integer memberCount, String meetMenu, String meetDate, List<String> departureList) {
        this.memberCount = memberCount;
        this.meetMenu = meetMenu;
        this.meetDate = meetDate;
        this.departureList = departureList;
    }
}
