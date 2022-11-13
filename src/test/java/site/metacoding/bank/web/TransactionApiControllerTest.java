package site.metacoding.bank.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import site.metacoding.bank.beans.DummyBeans;
import site.metacoding.bank.config.enums.TransactionEnum;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.transaction.TransactionRepository;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.domain.user.UserRepository;
import site.metacoding.bank.dto.transaction.TransactionReqDto.DepositReqDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.TransferReqDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.WithdrawReqDto;

@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class TransactionApiControllerTest extends DummyBeans {
        private final Logger log = LoggerFactory.getLogger(getClass());
        private static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";
        private static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded; charset=utf-8";

        // DI
        @Autowired
        private ObjectMapper om;
        @Autowired
        private MockMvc mvc;
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
                depositReqDto.setAmount(100L);
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
                withdrawReqDto.setWithdrawAccountId(1L);
                withdrawReqDto.setAmount(100L);
                withdrawReqDto.setAccountPassword("1234");
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
        public void transfer_test() throws Exception {
                // given
                Long userId = 1L;
                TransferReqDto transferReqDto = new TransferReqDto();
                transferReqDto.setWithdrawAccountId(1L);
                transferReqDto.setDepositAccountId(2L);
                transferReqDto.setAmount(100L);
                transferReqDto.setAccountPassword("1234");
                transferReqDto.setGubun("TRANSFER");
                String requestBody = om.writeValueAsString(transferReqDto);
                log.debug("디버그 : " + requestBody);

                // when
                ResultActions resultActions = mvc
                                .perform(post("/api/user/" + userId + "/transfer").content(requestBody)
                                                .contentType(APPLICATION_JSON_UTF8));
                String responseBody = resultActions.andReturn().getResponse().getContentAsString();
                log.debug("디버그 : " + responseBody);

                // then
                resultActions.andExpect(jsonPath("$.code").value(201));
                resultActions.andExpect(jsonPath("$.data.balance").value(900L));
                // 만약 아래에서 실패하면 롤백됨. 검증안해도 됨.
                // resultActions.andExpect(jsonPath("$.data.transaction.depositAccountBalance").value(1100L));
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
        public void transactionHistory_test() throws Exception {
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
                User ssarUser = userRepository.save(newUser("ssar"));
                User cosUser = userRepository.save(newUser("cos"));
                User adminUser = userRepository.save(newUser("admin"));
                Account ssarAccount1 = accountRepository.save(newAccount(1111L, "쌀", ssarUser));
                Account ssarAccount2 = accountRepository.save(newAccount(2222L, "쌀", ssarUser));
                Account cosAccount1 = accountRepository.save(newAccount(3333L, "코스", cosUser));
                Transaction withdrawTransaction1 = transactionRepository
                                .save(newWithdrawTransaction(100L, ssarAccount1));
                Transaction withdrawTransaction2 = transactionRepository
                                .save(newWithdrawTransaction(100L, ssarAccount1));
                Transaction depositTransaction1 = transactionRepository
                                .save(newDepositTransaction(100L, ssarAccount1));
                Transaction transferTransaction1 = transactionRepository
                                .save(newTransferTransaction(100L, ssarAccount1, cosAccount1));
                Transaction transferTransaction2 = transactionRepository
                                .save(newTransferTransaction(100L, ssarAccount1, ssarAccount2));
        }
}
