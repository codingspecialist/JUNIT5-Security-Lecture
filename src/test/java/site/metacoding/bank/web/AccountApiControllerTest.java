package site.metacoding.bank.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import site.metacoding.bank.config.WithAuthUser;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.domain.user.UserRepository;
import site.metacoding.bank.dto.AccountReqDto.AccountSaveReqDto;
import site.metacoding.bank.enums.UserEnum;

@Slf4j
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class AccountApiControllerTest {
    private static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";
    private static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded; charset=utf-8";

    private MockHttpSession session;

    // DI
    @Autowired
    private ObjectMapper om;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void dataInit() {
        log.debug("디버그 : dataInit");
        String encPassword = passwordEncoder.encode("1234");
        User user = User.builder().username("ssar").password(encPassword).email("ssar@nate.com").role(UserEnum.CUSTOMER)
                .build();
        userRepository.save(user);
    }

    // @WithMockUser // username=user, password=password role=ROLE_USER
    // @WithMockUser(username = "ssar", password = "1234", roles = {
    // "ROLE_CUSTOMER"})
    @WithAuthUser
    @Test
    public void save_test() throws Exception {
        // given
        AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
        accountSaveReqDto.setNumber(1112222L);
        accountSaveReqDto.setPassword("1234");

        String body = om.writeValueAsString(accountSaveReqDto);
        log.debug("디버그 : " + body);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/account").content(body).contentType(APPLICATION_JSON_UTF8));
        log.debug("디버그 : " + resultActions.andReturn().getResponse().getContentAsString());

        // then
        // resultActions.andExpect(jsonPath("$.code").value(201));

    }

}

// @BeforeEach
// public void dataInit() {
// String encPassword = sha256.encrypt("1234");
// User user = User.builder().username("ssar").password(encPassword).build();
// User userPS = userRepository.save(user);

// Board board = Board.builder()
// .title("스프링1강")
// .content("트랜잭션관리")
// .user(userPS)
// .build();
// Board boardPS = boardRepository.save(board);

// Comment comment1 = Comment.builder()
// .content("내용좋아요")
// .board(boardPS)
// .user(userPS)
// .build();

// Comment comment2 = Comment.builder()
// .content("내용싫어요")
// .board(boardPS)
// .user(userPS)
// .build();

// commentRepository.save(comment1);
// commentRepository.save(comment2);
// }

// @Test
// public void save_test() throws Exception {
// // given
// BoardSaveReqDto boardSaveReqDto = new BoardSaveReqDto();
// boardSaveReqDto.setTitle("스프링1강");
// boardSaveReqDto.setContent("트랜잭션관리");

// String body = om.writeValueAsString(boardSaveReqDto);

// // when
// ResultActions resultActions = mvc
// .perform(MockMvcRequestBuilders.post("/board").content(body)
// .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
// .session(session));

// // then
// MvcResult mvcResult = resultActions.andReturn();
// System.out.println("디버그 : " + mvcResult.getResponse().getContentAsString());
// resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1L));
// }