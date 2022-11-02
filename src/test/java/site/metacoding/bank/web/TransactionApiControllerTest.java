package site.metacoding.bank.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
import site.metacoding.bank.dto.transaction.TransactionReqDto.DepositReqDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.TransperReqDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.WithdrawReqDto;
import site.metacoding.bank.enums.TransactionEnum;
import site.metacoding.bank.enums.UserEnum;

@Slf4j
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class TransactionApiControllerTest {
        private static final String TAG = "TransactionApiControllerTest";
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
                User customer1 = User.builder().username("ssar").password(encPassword).email("ssar@nate.com")
                                .role(UserEnum.CUSTOMER)
                                .build();
                User customer1PS = userRepository.save(customer1);
                log.debug("디버그-" + TAG + " : ssar 유저 insert");

                User customer2 = User.builder().username("cos").password(encPassword).email("cos@nate.com")
                                .role(UserEnum.CUSTOMER)
                                .build();
                User customer2PS = userRepository.save(customer2);
                log.debug("디버그-" + TAG + " : cos 유저 insert");

                User admin = User.builder().username("admin").password(encPassword).email("admin@nate.com")
                                .role(UserEnum.ADMIN)
                                .build();
                userRepository.save(admin);
                log.debug("디버그-" + TAG + " : admin 유저 insert");

                Account account1 = Account.builder()
                                .number(111222L)
                                .password("1234")
                                .balance(100000L)
                                .user(customer1PS)
                                .build();

                Account account2 = Account.builder()
                                .number(333444L)
                                .password("1234")
                                .balance(100000L)
                                .user(customer1PS)
                                .build();

                Account account3 = Account.builder()
                                .number(6667777L)
                                .password("1234")
                                .balance(100000L)
                                .user(customer2PS)
                                .build();

                Account account1PS = accountRepository.save(account1);
                Account account2PS = accountRepository.save(account2);
                Account account3PS = accountRepository.save(account3);

                log.debug("디버그-" + TAG + " : 계좌 3개 insert");

                // ATM -> 계좌 (입금)
                Transaction transaction1 = Transaction.builder()
                                .withdrawAccount(null) // ATM -> 계좌
                                .depositAccount(account1PS)
                                .amount(10000L)
                                .depositAccountBalance(110000L)
                                .gubun(TransactionEnum.DEPOSIT)
                                .build();
                account1PS.addDepositTransaction(transaction1); // 트랜잭션 종료전 영속화 되기전 데이터 동기화
                account1PS.deposit(10000L); // 트랜잭션 종료전 영속화 되기전 데이터 동기화

                // 계좌 -> ATM (출금)
                Transaction transaction2 = Transaction.builder()
                                .withdrawAccount(account1PS) // 계좌 -> ATM
                                .depositAccount(null)
                                .amount(5000L)
                                .withdrawAccountBalance(105000L)
                                .gubun(TransactionEnum.WITHDRAW)
                                .build();
                account1PS.addWithdrawTransaction(transaction2); // 트랜잭션 종료전 영속화 되기전 데이터 동기화
                account1PS.withdraw(5000L);

                // 계좌1 -> 계좌2
                // 계좌1 입장에서 출금
                // 계좌2 입장에서 입금
                // 이체 (이체는 보는 관점에 따라 다름)
                Transaction transaction3 = Transaction.builder()
                                .withdrawAccount(account1PS) // 계좌 -> 계좌
                                .depositAccount(account3PS)
                                .amount(40000L)
                                .withdrawAccountBalance(65000L)
                                .depositAccountBalance(140000L)
                                .gubun(TransactionEnum.TRANSPER)
                                .build();
                account1PS.addWithdrawTransaction(transaction3); // 트랜잭션 종료전 영속화 되기전 데이터 동기화
                account1PS.withdraw(40000L);
                account3PS.addDepositTransaction(transaction3); // 트랜잭션 종료전 영속화 되기전 데이터 동기화
                account3PS.deposit(40000L);

                transactionRepository.save(transaction1);
                transactionRepository.save(transaction2);
                transactionRepository.save(transaction3);
                log.debug("디버그-" + TAG + " : 입출금이체 3개 insert");
        }

        @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        @Test
        public void withdraw_test() throws Exception {
                // given
                Long userId = 1L;
                WithdrawReqDto withdrawReqDto = new WithdrawReqDto();
                withdrawReqDto.setWithdrawAccountId(2L);
                withdrawReqDto.setAmount(1000L);
                withdrawReqDto.setGubun("WITHDRAW");
                String requestBody = om.writeValueAsString(withdrawReqDto);
                log.debug("디버그-" + TAG + " : " + requestBody);

                // when
                ResultActions resultActions = mvc
                                .perform(post("/api/user/" + userId + "/withdraw").content(requestBody)
                                                .contentType(APPLICATION_JSON_UTF8));
                String responseBody = resultActions.andReturn().getResponse().getContentAsString();
                log.debug("디버그-" + TAG + " : " + responseBody);

                // then
                resultActions.andExpect(jsonPath("$.code").value(201));
        }

        @Test
        public void deposit_test() throws Exception {
                // given
                DepositReqDto depositReqDto = new DepositReqDto();
                depositReqDto.setDepositAccountId(1L);
                depositReqDto.setAmount(1000L);
                depositReqDto.setGubun("DEPOSIT");
                String requestBody = om.writeValueAsString(depositReqDto);
                log.debug("디버그-" + TAG + " : " + requestBody);

                // when
                ResultActions resultActions = mvc
                                .perform(post("/api/deposit").content(requestBody)
                                                .contentType(APPLICATION_JSON_UTF8));
                String responseBody = resultActions.andReturn().getResponse().getContentAsString();
                log.debug("디버그-" + TAG + " : " + responseBody);

                // then
                resultActions.andExpect(jsonPath("$.code").value(201));
        }

        /*
         * 이체 (계좌 -> 계좌)
         */
        @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        @Test
        public void transper_test() throws Exception {
                // given
                Long userId = 1L;
                TransperReqDto transperReqDto = new TransperReqDto();
                transperReqDto.setWithdrawAccountId(2L);
                transperReqDto.setDepositAccountId(1L);
                transperReqDto.setAmount(5000L);
                transperReqDto.setGubun("TRANSPER");
                String requestBody = om.writeValueAsString(transperReqDto);
                log.debug("디버그-" + TAG + " : " + requestBody);

                // when
                ResultActions resultActions = mvc
                                .perform(post("/api/user/" + userId + "/transper").content(requestBody)
                                                .contentType(APPLICATION_JSON_UTF8));
                String responseBody = resultActions.andReturn().getResponse().getContentAsString();
                log.debug("디버그-" + TAG + " : " + responseBody);

                // then
                resultActions.andExpect(jsonPath("$.code").value(201));
        }

        /*
         * 출금 내역 보기 (입금, 출금은 ATM, 이체는 상대방 유저네임 필요)
         */
        @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        @Test
        public void withdrawHistory_test() throws Exception {
                // given
                Long userId = 1L;
                Long accountId = 1L;

                // when
                ResultActions resultActions = mvc
                                .perform(get("/api/user/" + userId + "/account/" + accountId + "/withdraw"));
                String responseBody = resultActions.andReturn().getResponse().getContentAsString();
                log.debug("디버그-" + TAG + " : " + responseBody);

                // then

        }

        // 입금 내역 보기

        // 입출금 내역 보기

}
