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
import site.metacoding.bank.config.enums.TransactionEnum;
import site.metacoding.bank.config.enums.UserEnum;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.transaction.TransactionRepository;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.domain.user.UserRepository;
import site.metacoding.bank.dto.transaction.TransactionReqDto.DepositReqDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.TransperReqDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.WithdrawReqDto;

@Slf4j
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class TransactionApiControllerTest {
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
        public void setUp() {
                dataSetting();
        }

        @Test
        public void deposit_test() throws Exception {
                // given
                DepositReqDto depositReqDto = new DepositReqDto();
                depositReqDto.setDepositAccountId(1L);
                depositReqDto.setAmount(1000L);
                depositReqDto.setGubun("DEPOSIT");
                String requestBody = om.writeValueAsString(depositReqDto);
                log.debug("디버그 : " + requestBody);

                // when
                ResultActions resultActions = mvc
                                .perform(post("/api/deposit").content(requestBody)
                                                .contentType(APPLICATION_JSON_UTF8));
                String responseBody = resultActions.andReturn().getResponse().getContentAsString();
                log.debug("디버그 : " + responseBody);

                // then
                resultActions.andExpect(jsonPath("$.code").value(201));
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
                log.debug("디버그 : " + requestBody);

                // when
                ResultActions resultActions = mvc
                                .perform(post("/api/user/" + userId + "/withdraw").content(requestBody)
                                                .contentType(APPLICATION_JSON_UTF8));
                String responseBody = resultActions.andReturn().getResponse().getContentAsString();
                log.debug("디버그 : " + responseBody);

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
                log.debug("디버그 : " + requestBody);

                // when
                ResultActions resultActions = mvc
                                .perform(post("/api/user/" + userId + "/transper").content(requestBody)
                                                .contentType(APPLICATION_JSON_UTF8));
                String responseBody = resultActions.andReturn().getResponse().getContentAsString();
                log.debug("디버그 : " + responseBody);

                // then
                resultActions.andExpect(jsonPath("$.code").value(201));
        }

        /*
         * 입금 내역 보기
         */
        @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        @Test
        public void transactionDepositHistory_test() throws Exception {
                // given
                Long userId = 1L;
                Long accountId = 1L;

                // when
                ResultActions resultActions = mvc
                                .perform(get("/api/user/" + userId + "/account/" + accountId + "/transaction")
                                                .param("gubun", TransactionEnum.DEPOSIT.name()));
                String responseBody = resultActions.andReturn().getResponse().getContentAsString();
                log.debug("디버그 : " + responseBody);

                // then
                resultActions.andExpect(jsonPath("$.code").value(200));
        }

        /*
         * 출금 내역 보기
         * 양방향 매핑시 : 테스트 시에는 dataInit()에서 순수 객체에 값을 미리 대입해두어서 Transaction이 1차 캐싱되고 쿼리가
         * 날라가지 않음.
         */
        @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        @Test
        public void transactionWithdrawHistory_test() throws Exception {
                // given
                Long userId = 1L;
                Long accountId = 1L;

                // when
                ResultActions resultActions = mvc
                                .perform(get("/api/user/" + userId + "/account/" + accountId + "/transaction")
                                                .param("gubun", TransactionEnum.WITHDRAW.name()));
                String responseBody = resultActions.andReturn().getResponse().getContentAsString();
                log.debug("디버그 : " + responseBody);

                // then
                resultActions.andExpect(jsonPath("$.code").value(200));
        }

        /*
         * 입출금 내역 보기
         */
        @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
        @Test
        public void transactionAllHistory_test() throws Exception {
                // given
                Long userId = 1L;
                Long accountId = 1L;

                // when
                ResultActions resultActions = mvc
                                .perform(get("/api/user/" + userId + "/account/" + accountId + "/transaction"));
                String responseBody = resultActions.andReturn().getResponse().getContentAsString();
                log.debug("디버그 : " + responseBody);

                // then
                resultActions.andExpect(jsonPath("$.code").value(200));
        }

        public void dataSetting() {
                String encPassword = passwordEncoder.encode("1234");
                User customer1 = User.builder().username("ssar").password(encPassword).email("ssar@nate.com")
                                .role(UserEnum.CUSTOMER)
                                .build();
                User customer1PS = userRepository.save(customer1);
                log.debug("디버그 : id:1, username: ssar 유저 생성");

                User customer2 = User.builder().username("cos").password(encPassword).email("cos@nate.com")
                                .role(UserEnum.CUSTOMER)
                                .build();
                User customer2PS = userRepository.save(customer2);
                log.debug("디버그 : id:2, username: cos 유저 생성");

                User admin = User.builder().username("admin").password(encPassword).email("admin@nate.com")
                                .role(UserEnum.ADMIN)
                                .build();
                userRepository.save(admin);
                log.debug("디버그 : id:3, username: admin 관리자 생성");

                Account account1 = Account.builder()
                                .number(1111L)
                                .password("1234")
                                .balance(100000L)
                                .user(customer1PS)
                                .build();
                Account account1PS = accountRepository.save(account1);
                log.debug("디버그 : ssar 고객 1111 계좌 생성 , 잔액 100000");

                Account account2 = Account.builder()
                                .number(2222L)
                                .password("1234")
                                .balance(100000L)
                                .user(customer1PS)
                                .build();
                Account account2PS = accountRepository.save(account2);
                log.debug("디버그 : ssar 고객 2222 계좌 생성 , 잔액 100000");

                Account account3 = Account.builder()
                                .number(3333L)
                                .password("1234")
                                .balance(100000L)
                                .user(customer2PS)
                                .build();
                Account account3PS = accountRepository.save(account3);
                log.debug("디버그 : cos 고객 3333 계좌 생성 , 잔액 100000");

                // ATM -> 계좌 (입금)
                Transaction transaction1 = Transaction.builder()
                                .withdrawAccount(null) // ATM -> 계좌
                                .depositAccount(account1PS)
                                .amount(10000L)
                                .depositAccountBalance(110000L)
                                .gubun(TransactionEnum.DEPOSIT)
                                .build();
                Transaction trasactionPS1 = transactionRepository.save(transaction1);
                account1PS.deposit(trasactionPS1);
                log.debug("디버그 : ATM -> 1111계좌(ssar), 입금액 : 10000, 잔액 : 110000 ");

                // 계좌 -> ATM (출금)
                Transaction transaction2 = Transaction.builder()
                                .withdrawAccount(account1PS) // 계좌 -> ATM
                                .depositAccount(null)
                                .amount(5000L)
                                .withdrawAccountBalance(105000L)
                                .gubun(TransactionEnum.WITHDRAW)
                                .build();
                Transaction trasactionPS2 = transactionRepository.save(transaction2);
                account1PS.withdraw(trasactionPS2);
                log.debug("디버그 : 1111계좌(ssar) -> ATM, 출금액 : 5000, 잔액 : 105000 ");

                // 계좌1 -> 계좌3
                Transaction transaction3 = Transaction.builder()
                                .withdrawAccount(account1PS) // 계좌 -> 계좌
                                .depositAccount(account3PS)
                                .amount(60000L)
                                .withdrawAccountBalance(45000L)
                                .depositAccountBalance(160000L)
                                .gubun(TransactionEnum.TRANSPER)
                                .build();
                Transaction trasactionPS3 = transactionRepository.save(transaction3);
                account1PS.withdraw(trasactionPS3);
                account3PS.deposit(trasactionPS3);
                log.debug("디버그 : 1111계좌(ssar) -> 3333계좌(cos), 이체액 : 60000, 1111계좌잔액 : 45000 , 3333계좌잔액: 160000");

                // 계좌2 -> 계좌1
                Transaction transaction4 = Transaction.builder()
                                .withdrawAccount(account2PS) // 계좌 -> 계좌
                                .depositAccount(account1PS)
                                .amount(30000L)
                                .withdrawAccountBalance(70000L)
                                .depositAccountBalance(75000L)
                                .gubun(TransactionEnum.TRANSPER)
                                .build();
                Transaction trasactionPS4 = transactionRepository.save(transaction4);
                account2PS.withdraw(trasactionPS4);
                account1PS.deposit(trasactionPS4);
                log.debug("디버그 : 2222계좌(ssar) -> 1111계좌(ssar), 이체액 : 30000, 1111계좌잔액 : 75000 , 2222계좌잔액: 70000");
        }
}
