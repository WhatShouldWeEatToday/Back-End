package kit.project.whatshouldweeattoday.domain.dto.review;

import lombok.Getter;

@Getter
public class MsgResponseDTO {
    private String msg;
    private int statusCode;

    public MsgResponseDTO(String msg, int statusCode) {
        this.msg = msg;
        this.statusCode = statusCode;
    }
}
