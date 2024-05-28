package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.notice.NoticeRequestDTO;
import kit.project.whatshouldweeattoday.domain.entity.Notice;
import kit.project.whatshouldweeattoday.domain.type.NoticeType;
import kit.project.whatshouldweeattoday.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/{loginId}/{noticeType}")
    public List<Notice> getNoticesByType(@PathVariable(name = "loginId") String loginId, @PathVariable(name = "noticeType") NoticeType noticeType) {
        return noticeService.getNoticesByType(loginId, noticeType);
    }

    @PostMapping("/send")
    public void sendNotice(@RequestBody NoticeRequestDTO noticeRequestDTO) {
        noticeService.sendNotice(
                noticeRequestDTO.getLoginId(),
                noticeRequestDTO.getNoticeType(),
                noticeRequestDTO.getContent()
        );
    }
}
