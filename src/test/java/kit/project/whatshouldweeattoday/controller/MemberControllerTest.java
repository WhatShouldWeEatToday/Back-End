package kit.project.whatshouldweeattoday.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import kit.project.whatshouldweeattoday.domain.dto.member.signup.SignupRequestDTO;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.repository.MemberRepository;
import kit.project.whatshouldweeattoday.service.MemberService;
import org.springframework.beans.factory.annotation.Value;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired MemberRepository memberRepository;
    @Autowired MemberService memberService;
    @Autowired EntityManager em;
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired PasswordEncoder passwordEncoder;

    private static String SIGN_UP_URL = "/api/signup";

    private String loginId = "test1234";
    private String loginPw = "Aa12345678";
    private String verifiedLoginPw = "Aa12345678";
    private String nickname = "사용자1";
    private String gender = "FEMALE";
    private Integer age = 100;

    private void clear(){
        em.flush();
        em.clear();
    }

    private void signUp(String signUpData) throws Exception {
        mockMvc.perform(
                        post(SIGN_UP_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(signUpData))
                .andExpect(status().isOk());
    }

    @Value("${jwt.access.header}")
    private String accessHeader;

    private static final String BEARER = "Bearer";

    private String getAccessToken() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("loginId",loginId);
        map.put("loginPw",loginPw);


        MvcResult result = mockMvc.perform(
                        post("/api/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk()).andReturn();

        return result.getResponse().getHeader(accessHeader);
    }

    @Test
    void signup() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new SignupRequestDTO(loginId, loginPw, verifiedLoginPw, nickname, gender, age));

        //when
        signUp(signUpData);

        //then
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new Exception("회원이 없습니다"));
        assertThat(member.getNickname()).isEqualTo(nickname);
        assertThat(memberRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    void updateUser() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new SignupRequestDTO(loginId, loginPw, verifiedLoginPw, nickname, gender, age));
        signUp(signUpData);

        String accessToken = getAccessToken();
        Map<String, Object> map = new HashMap<>();
        map.put("nickname", nickname + "변경");
        map.put("gender", gender + "변경");
        map.put("age", age + 1);
        String updateMemberData = objectMapper.writeValueAsString(map);


        //when
        mockMvc.perform(
                        patch("/api/user/update")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateMemberData))
                .andExpect(status().isOk());

        //then
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new Exception("회원이 없습니다"));
        assertThat(member.getNickname()).isEqualTo(nickname+"변경");
        assertThat(member.getGender()).isEqualTo(gender+"변경");
        assertThat(member.getAge()).isEqualTo(age + 1);
        assertThat(memberRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    void deleteUser() throws Exception {
        //given
        String signUpData = objectMapper.writeValueAsString(new SignupRequestDTO(loginId, loginPw, verifiedLoginPw, nickname, gender, age));
        signUp(signUpData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkPassword", loginPw);

        String updatePassword = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        delete("/api/user/delete")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isOk());

        //then
        assertThrows(Exception.class, () -> memberRepository.findByLoginId(loginId).orElseThrow(() -> new Exception("회원이 없습니다")));
    }
}