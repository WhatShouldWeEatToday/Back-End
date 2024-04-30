package kit.project.whatshouldweeattoday.security.service;

import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));
//        return new UserDetailsImpl(member.getLoginId(), member.getLoginPw());
        return User.builder()
                .username(member.getLoginId())
                .roles(member.getRole().name())
                .build();
    }
}
