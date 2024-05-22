package kit.project.whatshouldweeattoday.domain.dto.restaurant;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PathRequest {
    private String keyword;
    private List<String> startAddress;
    private String searchDttm;
}