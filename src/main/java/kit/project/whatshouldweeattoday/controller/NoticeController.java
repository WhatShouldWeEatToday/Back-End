package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.notice.NoticeRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.notice.NoticeResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Notice;
import kit.project.whatshouldweeattoday.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;


    @GetMapping("/{loginId}")
    public List<NoticeResponseDTO> getNoticesByType(@PathVariable(name = "loginId") String loginId) {
        return noticeService.getNoticesByMember(loginId);
    }

    @PostMapping("/send")
    public NoticeResponseDTO sendNotice(@RequestBody NoticeRequestDTO noticeRequestDTO) {
        return noticeService.sendNotice(
                noticeRequestDTO.getLoginId(),
                noticeRequestDTO.getContent(),
                noticeRequestDTO.getCreatedDate()
        );
    }


}
