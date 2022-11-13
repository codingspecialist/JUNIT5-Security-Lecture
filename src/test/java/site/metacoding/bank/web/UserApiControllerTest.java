package site.metacoding.bank.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import site.metacoding.bank.beans.DummyBeans;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.domain.user.UserRepository;
import site.metacoding.bank.dto.user.UserReqDto.UserJoinReqDto;

@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class UserApiControllerTest extends DummyBeans {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";
    private static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded; charset=utf-8";

    // DI
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        dataSetting();
    }

    /**
     * 회원가입
     */
    @Test
    public void join_test() throws Exception {
        // given
        UserJoinReqDto userJoinReqDto = new UserJoinReqDto();
        userJoinReqDto.setUsername("love");
        userJoinReqDto.setPassword("1234");
        userJoinReqDto.setEmail("love@nate.com");

        String requestBody = om.writeValueAsString(userJoinReqDto);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/join").content(requestBody).contentType(APPLICATION_JSON_UTF8));
        String body = resultActions.andReturn().getResponse().getContentAsString();
        log.debug("디버그 : " + body);
        // then
        resultActions.andExpect(jsonPath("$.code").value(201));
    }

    /**
     * 로그인
     */
    @Test
    public void login_test() throws Exception {
        // given
        String requestBody = "username=ssar&password=1234";

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/login").content(requestBody)
                        .contentType(APPLICATION_FORM_URLENCODED));
        String body = resultActions.andReturn().getResponse().getContentAsString();
        log.debug("디버그 : " + body);

        // then
        resultActions.andExpect(jsonPath("$.code").value(200));

    }

    public void dataSetting() {
        User ssarUser = userRepository.save(newUser("ssar"));
        User cosUser = userRepository.save(newUser("cos"));
        User adminUser = userRepository.save(newUser("admin"));
    }

}
