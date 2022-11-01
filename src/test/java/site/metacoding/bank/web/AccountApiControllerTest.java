package site.metacoding.bank.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
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
    private static final String TAG = "AccountApiControllerTest";
    private static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";
    private static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded; charset=utf-8";

    // DI
    @Autowired
    private ObjectMapper om;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    public void dataInit() {
        String encPassword = passwordEncoder.encode("1234");
        User user = User.builder().username("ssar").password(encPassword).email("ssar@nate.com").role(UserEnum.CUSTOMER)
                .build();
        User userPS = userRepository.save(user);
        log.debug("디버그-" + TAG + " : ssar 유저 insert");

        Account account1 = Account.builder()
                .number(111222L)
                .password("1234")
                .balance(0L)
                .user(userPS)
                .build();

        Account account2 = Account.builder()
                .number(333444L)
                .password("1234")
                .balance(0L)
                .user(userPS)
                .build();
        List<Account> accounts = Arrays.asList(account1, account2);
        accountRepository.saveAll(accounts);
        log.debug("디버그-" + TAG + " : 계좌 두개 insert");
    }

    /**
     * 계좌등록
     */
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void save_test() throws Exception {
        // given
        AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
        accountSaveReqDto.setNumber(55556666L);
        accountSaveReqDto.setPassword("1234");

        String requestBody = om.writeValueAsString(accountSaveReqDto);
        log.debug("디버그-" + TAG + " : " + requestBody);

        // when
        ResultActions resultActions = mvc
                .perform(post("/api/account").content(requestBody).contentType(APPLICATION_JSON_UTF8));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.debug("디버그-" + TAG + " : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.code").value(201));
    }

    /**
     * 본인 계좌목록
     */
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void list_test() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc
                .perform(get("/api/account"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        log.debug("디버그-" + TAG + " : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 본인 계좌 상세보기
     */
    @Test
    public void detail_test() {
        // given

        // when

        // then

    }
}
