package site.metacoding.bank.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Arrays;
import java.util.List;

import org.apache.naming.TransactionRef;
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
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.transaction.TransactionRepository;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.domain.user.UserRepository;
import site.metacoding.bank.dto.AccountReqDto.AccountSaveReqDto;
import site.metacoding.bank.enums.TransactionEnum;
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
        @Autowired
        private TransactionRepository transactionRepository;

        @BeforeEach
        public void dataInit() {
                String encPassword = passwordEncoder.encode("1234");
                User customer = User.builder().username("ssar").password(encPassword).email("ssar@nate.com")
                                .role(UserEnum.CUSTOMER)
                                .build();
                User customerPS = userRepository.save(customer);
                log.debug("디버그-" + TAG + " : ssar 유저 insert");
                User admin = User.builder().username("admin").password(encPassword).email("admin@nate.com")
                                .role(UserEnum.ADMIN)
                                .build();
                userRepository.save(admin);
                log.debug("디버그-" + TAG + " : admin 유저 insert");

                Account account1 = Account.builder()
                                .number(111222L)
                                .password("1234")
                                .balance(0L)
                                .user(customerPS)
                                .build();

                Account account2 = Account.builder()
                                .number(333444L)
                                .password("1234")
                                .balance(0L)
                                .user(customerPS)
                                .build();
                List<Account> accounts = Arrays.asList(account1, account2);
                accountRepository.saveAll(accounts);
                log.debug("디버그-" + TAG + " : 계좌 2개 insert");

                // ATM -> 계좌 (입금)
                Transaction transaction1 = Transaction.builder()
                                .withdrawAccount(null) // ATM
                                .depositAccount(account1) // 계좌
                                .amount(10000L)
                                .gubun(TransactionEnum.DEPOSIT)
                                .build();

                // 계좌 -> ATM (출금)
                Transaction transaction2 = Transaction.builder()
                                .withdrawAccount(account1) // 계좌
                                .depositAccount(null) // ATM
                                .amount(10000L)
                                .gubun(TransactionEnum.WITHDRAW)
                                .build();

                // 계좌1 -> 계좌2
                // 계좌1 입장에서 출금
                // 계좌2 입장에서 입금
                // 이체 (이체는 보는 관점에 따라 다름)
                Transaction transaction3 = Transaction.builder()
                                .withdrawAccount(account1) // 계좌
                                .depositAccount(account2) // 계좌
                                .amount(10000L)
                                .gubun(TransactionEnum.TRANSPER)
                                .build();

                List<Transaction> transactions = Arrays.asList(transaction1, transaction2, transaction3);
                transactionRepository.saveAll(transactions);
                log.debug("디버그-" + TAG + " : 입출금이체 3개 insert");
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
                                .perform(get("/api/user/1/account"));
                String responseBody = resultActions.andReturn().getResponse().getContentAsString();
                log.debug("디버그-" + TAG + " : " + responseBody);

                // then
                resultActions.andExpect(jsonPath("$.code").value(200));
        }

        /**
         * 본인 계좌 상세보기
         */
        @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        @Test
        public void detail_test() throws Exception {
                // given
                Long accountId = 1L;
                Long userId = 1L;

                // when
                ResultActions resultActions = mvc
                                .perform(get("/api/user/" + userId + "/account/" + accountId));
                String responseBody = resultActions.andReturn().getResponse().getContentAsString();
                log.debug("디버그-" + TAG + " : " + responseBody);

                // then
                resultActions.andExpect(jsonPath("$.code").value(200));
        }

        @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        @Test
        public void delete_test() throws Exception {
                // given
                Long accountId = 1L;
                Long userId = 1L;

                // when
                ResultActions resultActions = mvc
                                .perform(delete("/api/user/" + userId + "/account/" + accountId));
                String responseBody = resultActions.andReturn().getResponse().getContentAsString();
                log.debug("디버그-" + TAG + " : " + responseBody);

                // then
                resultActions.andExpect(jsonPath("$.code").value(200));

        }
}
