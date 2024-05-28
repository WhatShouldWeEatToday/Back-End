package kit.project.whatshouldweeattoday.domain.dto.notice;

import kit.project.whatshouldweeattoday.domain.type.NoticeType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeRequestDTO {

    private String loginId;
    private NoticeType noticeType;
    private String content;
}