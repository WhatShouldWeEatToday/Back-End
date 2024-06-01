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
    private final SimpMessagingTemplate messagingTemplate;

   /* public List<Notice> getNoticesByMember(String loginId) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return noticeRepository.findAllByMember(member);
    }

    public Notice sendNotice(String loginId, String content, String createdDate) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        Notice notice = new Notice(member, content, createdDate);

        return noticeRepository.save(notice);
    }*/


    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public List<NoticeResponseDTO> getNoticesByMember(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        List<Notice> notices = noticeRepository.findAllByMember(member);

        return notices.stream().map(notice -> new NoticeResponseDTO(
                notice.getId(),
                notice.getMember().getId(),
                notice.getContent(),
                notice.getCreatedDate()
        )).collect(Collectors.toList());
    }

    public NoticeResponseDTO sendNotice(String loginId, String content, String createdDate) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 현재 날짜를 String으로 변환
        String dateString = dateFormat.format(new Date());

        Notice notice = new Notice(member, content, dateString);
        noticeRepository.save(notice);

        NoticeResponseDTO responseDTO = new NoticeResponseDTO(
                notice.getId(),
                notice.getMember().getId(),
                notice.getContent(),
                notice.getCreatedDate()
        );

        messagingTemplate.convertAndSendToUser(member.getLoginId(), "/topic/notices", responseDTO);

        return responseDTO;
    }
}
