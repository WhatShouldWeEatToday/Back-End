package kit.project.whatshouldweeattoday.service;

import jakarta.persistence.EntityManager;
import kit.project.whatshouldweeattoday.domain.dto.member.signup.SignupRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.member.update.MemberUpdateRequestDTO;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.domain.type.RoleType;
import kit.project.whatshouldweeattoday.repository.MemberRepository;
import kit.project.whatshouldweeattoday.security.util.SecurityUtil;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberRepository memberRepository;
    @Autowired MemberService memberService;
    @Autowired EntityManager em;
    @Autowired PasswordEncoder passwordEncoder;
    String loginPw = "Aa12345678";

    private void clear() {
        em.flush();
        em.clear();
    }

    private SignupRequestDTO createSignupRequestDTO() {
        return new SignupRequestDTO("test1234", loginPw, "Aa12345678", "사용자1","FEMALE", 22);
    }

    public SignupRequestDTO setMember() throws BadRequestException {
        SignupRequestDTO signupRequestDTO = createSignupRequestDTO();
        memberService.createMember(signupRequestDTO);
        clear();
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();

        emptyContext.setAuthentication(new UsernamePasswordAuthenticationToken(User.builder()
                .username(signupRequestDTO.getLoginId())
                .password(signupRequestDTO.getLoginPw())
                .roles(RoleType.USER.name())
                .build(),
                null, null));

        SecurityContextHolder.setContext(emptyContext);
        return signupRequestDTO;
    }

    @AfterEach
    public void removeUser(){
        SecurityContextHolder.createEmptyContext().setAuthentication(null);
    }


    @Test
    @DisplayName("회원가입")
    public void signup() throws BadRequestException {
        // given
        SignupRequestDTO signupRequestDTO = createSignupRequestDTO();

        // when
        memberService.createMember(signupRequestDTO);
        clear();

        // then
        Member member = memberRepository.findByLoginId(signupRequestDTO.getLoginId()).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));
        assertThat(member.getId()).isNotNull();
        assertThat(member.getLoginId()).isEqualTo(signupRequestDTO.getLoginId());
        assertThat(member.getNickname()).isEqualTo(signupRequestDTO.getNickname());
        assertThat(member.getGender()).isEqualTo(signupRequestDTO.getGender());
        assertThat(member.getAge()).isEqualTo(signupRequestDTO.getAge());
        assertThat(member.getRole()).isSameAs(RoleType.USER);
    }

    @Test
    @DisplayName("아이디_중복_검사")
    public void checkDuplicateId() {
        // given
        Member member1 = Member.builder()
                .loginId("test1")
                .loginPw("1234")
                .verifiedLoginPw("1234")
                .nickname("사용자1")
                .gender("MALE")
                .age(20)
                .role(RoleType.USER)
                .build();

        Member member2 = Member.builder()
                .loginId("test1")
                .loginPw("1234")
                .verifiedLoginPw("1234")
                .nickname("사용자2")
                .gender("FEMALE")
                .age(24)
                .role(RoleType.USER)
                .build();

        memberRepository.save(member1);
        clear();

        // when, then
        assertThrows(Exception.class, () -> memberRepository.save(member2));
    }

    @Test
    @DisplayName("닉네임_중복_검사")
    public void checkDuplicateNickname() {
        // given
        Member member1 = Member.builder()
                .loginId("test1")
                .loginPw("1234")
                .verifiedLoginPw("1234")
                .nickname("사용자1")
                .gender("MALE")
                .age(20)
                .role(RoleType.USER)
                .build();

        Member member2 = Member.builder()
                .loginId("test2")
                .loginPw("1234")
                .verifiedLoginPw("1234")
                .nickname("사용자1")
                .gender("FEMALE")
                .age(24)
                .role(RoleType.USER)
                .build();

        memberRepository.save(member1);
        clear();

        // when, then
        assertThrows(Exception.class, () -> memberRepository.save(member2));
    }

    @Test
    @DisplayName("회원_정보_수정(닉네임, 성별, 나이)")
    public void updateUserInfo() throws Exception {
        // given
        SignupRequestDTO signupRequestDTO = setMember();

        // when
        String updateNickname = "변경된닉네임";
        String updateGender = "변경된성별";
        Integer updateAge = 50;

        memberService.updateMember(new MemberUpdateRequestDTO(Optional.of(updateNickname), Optional.of(updateGender), Optional.of(updateAge)), SecurityUtil.getLoginId());
        clear();

        // then
        memberRepository.findByLoginId(signupRequestDTO.getLoginId()).ifPresent((member -> {
            assertThat(member.getNickname()).isEqualTo(updateNickname);
            assertThat(member.getGender()).isEqualTo(updateGender);
            assertThat(member.getAge()).isEqualTo(updateAge);
            assertThat(member.getLoginId()).isEqualTo(signupRequestDTO.getLoginId());
        }));
    }

    @Test
    @DisplayName("회원탈퇴")
    public void deleteUser() throws Exception {
        // given
        SignupRequestDTO signupRequestDTO = setMember();

        // when
        memberService.deleteMember(loginPw, SecurityUtil.getLoginId());

        // then
        assertThat(assertThrows(Exception.class, ()-> memberRepository.findByLoginId(signupRequestDTO.getLoginId()).orElseThrow(() -> new Exception("회원이 없습니다"))).getMessage()).isEqualTo("회원이 없습니다");
    }
}