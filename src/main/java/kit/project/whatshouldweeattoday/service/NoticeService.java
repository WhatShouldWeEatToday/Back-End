package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.domain.entity.Notice;
import kit.project.whatshouldweeattoday.domain.type.NoticeType;
import kit.project.whatshouldweeattoday.repository.MemberRepository;
import kit.project.whatshouldweeattoday.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    public void sendNotice(String content, NoticeType noticeType, String loginId) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        Notice notice = new Notice(content, noticeType, member);
        noticeRepository.save(notice);
    }

    public List<Notice> getNoticesByType(String loginId, NoticeType noticeType) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return noticeRepository.findAllByMemberAndNoticeType(member, noticeType);
    }
}
