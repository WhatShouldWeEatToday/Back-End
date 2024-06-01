package kit.project.whatshouldweeattoday.domain.dto.notice;

import lombok.Getter;

@Getter
public class NoticeResponseDTO {
    private Long id;
    private Long memberId;
    private String createdDate;
    private String content;

    public NoticeResponseDTO(Long id, Long memberId, String content, String createdDate) {
        this.id = id;
        this.memberId = memberId;
        this.content = content;
        this.createdDate = createdDate;
    }
}
