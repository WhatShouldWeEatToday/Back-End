package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.notice.NoticeResponseDTO;
import kit.project.whatshouldweeattoday.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
