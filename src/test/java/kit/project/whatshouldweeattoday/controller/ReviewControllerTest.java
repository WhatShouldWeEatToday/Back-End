//package kit.project.whatshouldweeattoday.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.persistence.EntityManager;
//import kit.project.whatshouldweeattoday.domain.dto.member.signup.SignupRequestDTO;
//import kit.project.whatshouldweeattoday.domain.entity.Member;
//import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
//import kit.project.whatshouldweeattoday.domain.type.RoleType;
//import kit.project.whatshouldweeattoday.repository.MemberRepository;
//import kit.project.whatshouldweeattoday.repository.RestaurantRepository;
//import kit.project.whatshouldweeattoday.repository.ReviewRepository;
//import org.apache.coyote.BadRequestException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//class ReviewControllerTest {
//
//    @Autowired MockMvc mockMvc;
//    @Autowired EntityManager em;
////    @Autowired PasswordEncoder passwordEncoder;
//
//    @Autowired MemberRepository memberRepository;
//    @Autowired RestaurantRepository restaurantRepository;
//    @Autowired ReviewRepository reviewRepository;
//    @Autowired JwtService jwtService;
//    ObjectMapper objectMapper = new ObjectMapper();
//
//    private static Member member;
//
//    private static String SIGN_UP_URL = "/api/signup";
//
//    private String loginId = "test1234";
//    private String loginPw = "Aa12345678";
//    private String verifiedLoginPw = "Aa12345678";
//    private String nickname = "사용자1";
//    private String gender = "FEMALE";
//    private Integer age = 100;
//    private RoleType roleType = RoleType.USER;
//
//    private void clear(){
//        em.flush();
//        em.clear();
//    }
//
//    private void signUp(String signUpData) throws Exception {
//        mockMvc.perform(
//                        post(SIGN_UP_URL)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(signUpData))
//                .andExpect(status().isOk());
//    }
//
//    @BeforeEach
//    public void signUpMember() throws Exception {
//        //given
//        String signUpData = objectMapper.writeValueAsString(new SignupRequestDTO(loginId, loginPw, verifiedLoginPw, nickname, gender, age, roleType));
//
//        //when
//        signUp(signUpData);
//    }
//
//    @Value("${jwt.access.header}")
//    private String accessHeader;
//
//    private static final String BEARER = "Bearer";
//
//    private String getAccessToken() throws Exception {
//
//        Map<String, String> map = new HashMap<>();
//        map.put("loginId",loginId);
//        map.put("loginPw",loginPw);
//
//
//        MvcResult result = mockMvc.perform(
//                        post("/api/signin")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(map)))
//                .andExpect(status().isOk()).andReturn();
//
//        return result.getResponse().getHeader(accessHeader);
//    }
//
//    @Test
//    void save() throws Exception {
//        //given
//        Restaurant restaurant = restaurantRepository.findById(1L).orElseThrow(() -> new BadRequestException("존재하지 않는 음식점입니다."));
//
//        String accessToken = getAccessToken();
//        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//        map.add("taste", "5");
//        map.add("mood", "5");
//        map.add("park", "5");
//        map.add("kind", "5");
//        map.add("cost", "5");
//        map.add("stars", "5.0");
//        map.add("totalLikes", "10L");
//
//        //when
//        mockMvc.perform(
//                        post("/api/review/" + restaurant.getId())
//                                .header("Authorization", BEARER + accessToken)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(map)))
//                .andExpect(status().isOk()).andReturn();
//
//        //then
//        assertThat(reviewRepository.findAll().size()).isEqualTo(1);
//    }
//}