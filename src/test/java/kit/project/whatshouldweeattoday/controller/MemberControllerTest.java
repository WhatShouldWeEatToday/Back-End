//package kit.project.whatshouldweeattoday.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.persistence.EntityManager;
//import kit.project.whatshouldweeattoday.domain.dto.member.signup.SignupRequestDTO;
//import kit.project.whatshouldweeattoday.domain.entity.Member;
//import kit.project.whatshouldweeattoday.domain.type.RoleType;
//import kit.project.whatshouldweeattoday.repository.MemberRepository;
//import kit.project.whatshouldweeattoday.service.MemberService;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.springframework.beans.factory.annotation.Value;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@Transactional
//@Slf4j
//@AutoConfigureMockMvc
//class MemberControllerTest {
//
//    @Autowired MemberRepository memberRepository;
//    @Autowired MemberService memberService;
//    @Autowired EntityManager em;
//    @Autowired MockMvc mockMvc;
//
//    @BeforeEach
//    void beforeEach() {
//        SignupRequestDTO.builder()
//                .loginId("test131313")
//                .loginPw("a12345678")
//                .verifiedLoginPw("a12345678")
//                .nickname("사용자1111")
//                .gender("FEMALE")
//                .age(24)
//                .roleType(RoleType.USER)
//                .build();
//    }
//
//    private void clear(){
//        em.flush();
//        em.clear();
//    }
//
//    @Test
//    public void signUpTest() {
//
//        // API 요청 설정
//        String url = "http://localhost:" + randomServerPort + "/members/sign-up";
//        ResponseEntity<MemberDto> responseEntity = testRestTemplate.postForEntity(url, signUpDto, MemberDto.class);
//
//        // 응답 검증
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        MemberDto savedMemberDto = responseEntity.getBody();
//        assertThat(savedMemberDto.getUsername()).isEqualTo(signUpDto.getUsername());
//        assertThat(savedMemberDto.getNickname()).isEqualTo(signUpDto.getNickname());
//    }
//
//    @Test
//    @DisplayName("아이디_중복_검사")
//    public void checkDuplicateId() {
//        // given
//        Member member1 = Member.builder()
//                .loginId("test1")
//                .loginPw("1234")
//                .verifiedLoginPw("1234")
//                .nickname("사용자1")
//                .gender("MALE")
//                .age(20)
//                .role(RoleType.USER)
//                .build();
//
//        Member member2 = Member.builder()
//                .loginId("test1")
//                .loginPw("1234")
//                .verifiedLoginPw("1234")
//                .nickname("사용자2")
//                .gender("FEMALE")
//                .age(24)
//                .role(RoleType.USER)
//                .build();
//
//        memberRepository.save(member1);
//        clear();
//
//        // when, then
//        assertThrows(Exception.class, () -> memberRepository.save(member2));
//    }
//
//    @Test
//    @DisplayName("닉네임_중복_검사")
//    public void checkDuplicateNickname() {
//        // given
//        Member member1 = Member.builder()
//                .loginId("test1")
//                .loginPw("1234")
//                .verifiedLoginPw("1234")
//                .nickname("사용자1")
//                .gender("MALE")
//                .age(20)
//                .role(RoleType.USER)
//                .build();
//
//        Member member2 = Member.builder()
//                .loginId("test2")
//                .loginPw("1234")
//                .verifiedLoginPw("1234")
//                .nickname("사용자1")
//                .gender("FEMALE")
//                .age(24)
//                .role(RoleType.USER)
//                .build();
//
//        memberRepository.save(member1);
//        clear();
//
//        // when, then
//        assertThrows(Exception.class, () -> memberRepository.save(member2));
//    }
//
//    @Test
//    void updateUser() throws Exception {
//        //given
//        String signUpData = objectMapper.writeValueAsString(new SignupRequestDTO(loginId, loginPw, verifiedLoginPw, nickname, gender, age, RoleType.USER));
//        signUp(signUpData);
//
//        String accessToken = getAccessToken();
//        Map<String, Object> map = new HashMap<>();
//        map.put("nickname", nickname + "변경");
//        map.put("gender", gender + "변경");
//        map.put("age", age + 1);
//        String updateMemberData = objectMapper.writeValueAsString(map);
//
//
//        //when
//        mockMvc.perform(
//                        patch("/api/user/update")
//                                .header(accessHeader,BEARER+accessToken)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(updateMemberData))
//                .andExpect(status().isOk());
//
//        //then
//        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new Exception("회원이 없습니다"));
//        assertThat(member.getNickname()).isEqualTo(nickname+"변경");
//        assertThat(member.getGender()).isEqualTo(gender+"변경");
//        assertThat(member.getAge()).isEqualTo(age + 1);
//        assertThat(memberRepository.findAll().size()).isEqualTo(1);
//    }
//
//    @Test
//    void deleteUser() throws Exception {
//        //given
//        String signUpData = objectMapper.writeValueAsString(new SignupRequestDTO(loginId, loginPw, verifiedLoginPw, nickname, gender, age, RoleType.USER));
//        signUp(signUpData);
//
//        String accessToken = getAccessToken();
//
//        Map<String, Object> map = new HashMap<>();
//        map.put("checkPassword", loginPw);
//
//        String updatePassword = objectMapper.writeValueAsString(map);
//
//        //when
//        mockMvc.perform(
//                        delete("/api/user/delete")
//                                .header(accessHeader,BEARER+accessToken)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(updatePassword))
//                .andExpect(status().isOk());
//
//        //then
//        assertThrows(Exception.class, () -> memberRepository.findByLoginId(loginId).orElseThrow(() -> new Exception("회원이 없습니다")));
//    }
//}
//}