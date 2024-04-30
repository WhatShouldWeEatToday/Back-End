package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.member.signup.SignupRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.member.update.MemberUpdateRequestDTO;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.repository.MemberRepository;
import kit.project.whatshouldweeattoday.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createMember(SignupRequestDTO signupRequestDTO) throws BadRequestException {
        Member member = signupRequestDTO.toEntity();

        member.addUserAuthority();
        member.encodePassword(passwordEncoder);

        if(memberRepository.findByLoginId(signupRequestDTO.getLoginId()).isPresent()){
            throw new BadRequestException("이미 존재하는 아이디입니다.");
        }
        memberRepository.save(member);
    }

    @Transactional
    public boolean confirmId(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    @Transactional
    public boolean confirmNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    @Transactional
    public void updateMember(MemberUpdateRequestDTO requestDTO, String loginId) throws BadRequestException {
        Member member = memberRepository.findByLoginId(loginId) //SecurityContextHolder 에 들어있는 loginId 가져옴, TODO : 이거 변경함
                .orElseThrow(() -> new BadRequestException("존재하지 않는 회원입니다."));

        requestDTO.nickname().ifPresent(member::updateNickname);
        requestDTO.gender().ifPresent(member::updateGender);
        requestDTO.age().ifPresent(member::updateAge);
    }

    @Transactional
    public void deleteMember(String checkPassword, String loginId) throws Exception {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 회원입니다."));

        if(!member.matchPassword(passwordEncoder, checkPassword) ) {
            throw new BadRequestException("올바르지 않은 비밀번호입니다.");
        }
        memberRepository.delete(member);
    }

    @Transactional
    public Optional<Member> findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId);
    }
}
