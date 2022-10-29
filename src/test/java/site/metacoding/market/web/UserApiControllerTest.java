package site.metacoding.market.web;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import site.metacoding.market.dto.UserReqDto.UserJoinReqDto;

@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class UserApiControllerTest {
    private static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";

    // DI
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void join_test() throws Exception {
        // given
        UserJoinReqDto userJoinReqDto = new UserJoinReqDto();
        userJoinReqDto.setUsername("ssar");
        userJoinReqDto.setPassword("1234");
        userJoinReqDto.setEmail("ssar@nate.com");

        String body = om.writeValueAsString(userJoinReqDto);
        System.out.println("디버그 : " + body);

        // when
        ResultActions resultActions = mvc
                .perform(MockMvcRequestBuilders.post("/api/v1/join").content(body).contentType(APPLICATION_JSON_UTF8));
        System.out.println("디버그 : " + resultActions.andReturn().getResponse().getContentAsString());

        // then
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(201));
    }

}
