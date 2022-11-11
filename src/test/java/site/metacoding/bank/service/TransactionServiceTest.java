package site.metacoding.bank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import site.metacoding.bank.bean.AllEntityTest;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.transaction.TransactionRepository;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.dto.transaction.TransactionReqDto.DepositReqDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.WithdrawReqDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.DepositRespDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.WithdrawRespDto;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest extends AllEntityTest {
        private final Logger log = LoggerFactory.getLogger(getClass());

        @InjectMocks
        private TransactionService transactionService;

        @Mock
        private AccountRepository accountRepository;

        @Mock
        private TransactionRepository transactionRepository;

        @Spy
        private ObjectMapper om;

        /*
         * 구분값 검증 : setGubun("WITHDRAW") (완)
         * 잔액 확인 (1000원) (완)
         */
        @Test
        public void 입금하기_test() throws Exception {
                // given
                DepositReqDto depositReqDto = new DepositReqDto();
                depositReqDto.setDepositAccountId(1L);
                depositReqDto.setAmount(1000L);
                depositReqDto.setGubun("DEPOSIT");

                // stub
                User ssarUser = newUser(1L, "ssar");
                Account ssarAccount = newAccount(1L, 1111L, ssarUser);
                when(accountRepository.findById(any()))
                                .thenReturn(Optional.of(ssarAccount));

                Transaction transaction = newDepositTransaction(1L, ssarAccount);
                when(transactionRepository.save(any()))
                                .thenReturn(transaction);

                // when
                DepositRespDto depositRespDto = transactionService.입금하기(depositReqDto);
                String body = om.writeValueAsString(depositRespDto);
                log.debug("디버그 : " + body);

                // then
                assertThat(depositRespDto.getDepositAccount().getBalance()).isEqualTo(ssarAccount.getBalance());
        }

        /**
         * 구분값 검증 (완)
         * 출금계좌 소유자 확인 (완)
         * 잔액부족 여부 확인 (완)
         * 잔액확인 (1000원) (완)
         */
        @Test
        public void 출금하기_test() throws Exception {
                // given
                Long userId = 1L; // 출금계좌 소유자 확인 2로 변경

                WithdrawReqDto withdrawReqDto = new WithdrawReqDto();
                withdrawReqDto.setWithdrawAccountId(1L);
                withdrawReqDto.setAmount(100L); // 잔액부족 여부 확인
                withdrawReqDto.setGubun("WITHDRAW"); // 구분값 검증

                // stub
                User ssarUser = newUser(1L, "ssar");
                User cosUser = newUser(2L, "cos");
                User adminUser = newUser(3L, "admin");
                List<User> users = Arrays.asList(ssarUser, cosUser, adminUser);
                Account ssarAccount1 = newAccount(1L, 1111L, ssarUser);
                Account ssarAccount2 = newAccount(2L, 2222L, ssarUser);
                Account cosAccount1 = newAccount(3L, 3333L, cosUser);
                List<Account> accounts = Arrays.asList(ssarAccount1, ssarAccount2, cosAccount1);
                Transaction withdrawTransaction1 = newWithdrawTransaction(1L, ssarAccount1);
                Transaction withdrawTransaction2 = newWithdrawTransaction(2L, ssarAccount1);
                Transaction depositTransaction1 = newDepositTransaction(3L, ssarAccount1);
                Transaction transferTransaction1 = newTransferTransaction(4L, ssarAccount1, cosAccount1);
                List<Transaction> transactions = Arrays.asList(withdrawTransaction1, withdrawTransaction2,
                                depositTransaction1, transferTransaction1);

                when(accountRepository.findById(any()))
                                .thenReturn(Optional.of(ssarAccount1));

                when(transactionRepository.save(any()))
                                .thenReturn(withdrawTransaction1);

                // when
                WithdrawRespDto withdrawRespDto = transactionService.출금하기(withdrawReqDto, userId);
                String body = om.writeValueAsString(withdrawRespDto);
                log.debug("디버그 : " + body);

                // then (잔액 확인)
                assertThat(withdrawRespDto.getWithdrawAccount().getBalance()).isEqualTo(ssarAccount1.getBalance());
        }

        // 이체하기

        // 입출금목록보기
}