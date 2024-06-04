package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.notice.NoticeResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Likes;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.domain.entity.Notice;
import kit.project.whatshouldweeattoday.repository.MemberRepository;
import kit.project.whatshouldweeattoday.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    public List<NoticeResponseDTO> getNoticesByMember(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        List<Notice> notices = noticeRepository.findAllByMember(member);

        return notices.stream().map(notice -> new NoticeResponseDTO(
                notice.getId(),
                notice.getMember().getId(),
                notice.getContent(),
                notice.getNoticeType()
        )).collect(Collectors.toList());
    }
}
