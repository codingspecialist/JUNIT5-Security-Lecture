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

import site.metacoding.bank.beans.DummyMockBeans;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.transaction.TransactionRepository;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.dto.transaction.TransactionReqDto.DepositReqDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.TransferReqDto;
import site.metacoding.bank.dto.transaction.TransactionReqDto.WithdrawReqDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.DepositRespDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.TransactionListRespDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.TransferRespDto;
import site.metacoding.bank.dto.transaction.TransactionRespDto.WithdrawRespDto;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest extends DummyMockBeans {
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
         * 입금하기
         * insert, update, delete에서 하나의 stub은 다음 stub에 영향을 주면 안된다.
         * when에서 정의해둔 객체를 다른 곳에서 사용하게 되면 실행시점에 값이 변경될 수 있기 떄문이다.
         */
        @Test
        public void 입금하기_test() throws Exception {
                // given
                DepositReqDto depositReqDto = new DepositReqDto();
                depositReqDto.setDepositAccountId(1L);
                depositReqDto.setAmount(100L);
                depositReqDto.setGubun("DEPOSIT");

                // stub 1
                User ssarUser = newUser(1L, "ssar");
                Account ssarAccount = newAccount(1L, 1111L, "쌀", ssarUser);
                when(accountRepository.findById(any())).thenReturn(Optional.of(ssarAccount)); // 1000원

                // stub 2
                Account ssarAccountCopy = accountCopy(ssarAccount);
                Transaction transaction = newDepositTransaction(1L, depositReqDto.getAmount(), ssarAccountCopy);
                when(transactionRepository.save(any())).thenReturn(transaction); // 1100원

                // when
                DepositRespDto depositRespDto = transactionService.입금하기(depositReqDto);
                String body = om.writeValueAsString(depositRespDto);
                log.debug("디버그 : " + body);

                // then
                assertThat(depositRespDto.getBalance()).isEqualTo(1100L);
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
                withdrawReqDto.setAccountPassword("1234");
                withdrawReqDto.setGubun("WITHDRAW"); // 구분값 검증

                // stub 1
                User ssarUser = newUser(1L, "ssar");
                Account ssarAccount = newAccount(1L, 1111L, "쌀", ssarUser);
                when(accountRepository.findById(any())).thenReturn(Optional.of(ssarAccount));

                // stub 2
                Account ssarAccountCopy = accountCopy(ssarAccount);
                Transaction withdrawTransaction = newWithdrawTransaction(1L, 100L, ssarAccountCopy);
                when(transactionRepository.save(any())).thenReturn(withdrawTransaction);

                // when
                WithdrawRespDto withdrawRespDto = transactionService.출금하기(withdrawReqDto, userId);
                String body = om.writeValueAsString(withdrawRespDto);
                log.debug("디버그 : " + body);

                // then (잔액 확인)
                assertThat(withdrawRespDto.getBalance()).isEqualTo(900L);
        }

        // 이체하기
        @Test
        public void 이체하기_test() throws Exception {
                // given
                Long userId = 1L;

                TransferReqDto transferReqDto = new TransferReqDto();
                transferReqDto.setAccountPassword("1234");
                transferReqDto.setAmount(100L);
                transferReqDto.setWithdrawAccountId(1L);
                transferReqDto.setDepositAccountId(2L);
                transferReqDto.setGubun("TRANSFER");

                // stub 1
                User ssarUser = newUser(1L, "ssar");
                Account ssarAccount = newAccount(1L, 1111L, "쌀", ssarUser);
                when(accountRepository.findById(1L)).thenReturn((Optional.of(ssarAccount)));

                // stub 2
                User cosUser = newUser(2L, "cos");
                Account cosAccount = newAccount(2L, 3333L, "코스", cosUser);
                when(accountRepository.findById(2L)).thenReturn((Optional.of(cosAccount)));

                // stub 3
                Account ssarAccountCopy = accountCopy(ssarAccount);
                Account cosAccountCopy = accountCopy(cosAccount);
                Transaction transferTransaction = newTransferTransaction(1L, 100L, ssarAccountCopy, cosAccountCopy);
                when(transactionRepository.save(any())).thenReturn(transferTransaction);

                // when
                TransferRespDto transferRespDto = transactionService.이체하기(transferReqDto, userId);
                String body = om.writeValueAsString(transferRespDto);
                log.debug("디버그 : " + body);

                // then
                assertThat(transferRespDto.getBalance()).isEqualTo(transferTransaction.getWithdrawAccountBalance());
        }

        // 입출금목록보기
        @Test
        public void 입출금목록보기_test() throws Exception {
                // given
                Long userId = 1L;
                Long accountId = 1L;
                String gubun = null;
                Integer page = 1;

                // stub
                User ssarUser = newUser(1L, "ssar");
                User cosUser = newUser(2L, "cos");
                Account ssarAccount1 = newAccount(1L, 1111L, "쌀", ssarUser);
                Account ssarAccount2 = newAccount(2L, 2222L, "쌀", ssarUser);
                Account cosAccount1 = newAccount(3L, 3333L, "코스", cosUser);
                Transaction withdrawTransaction1 = newWithdrawTransaction(1L, 100L, ssarAccount1);
                Transaction withdrawTransaction2 = newWithdrawTransaction(2L, 100L, ssarAccount1);
                Transaction depositTransaction1 = newDepositTransaction(3L, 100L, ssarAccount1);
                Transaction transferTransaction1 = newTransferTransaction(4L, 100L, ssarAccount1, cosAccount1);
                Transaction transferTransaction2 = newTransferTransaction(5L, 100L, ssarAccount1, ssarAccount2);
                List<Transaction> transactions = Arrays.asList(transferTransaction1, transferTransaction2); // 뒤에 2개만
                                                                                                            // 추가함

                when(accountRepository.findById(any())).thenReturn((Optional.of(ssarAccount1)));
                when(transactionRepository.findByAccountId(any(), any(), any())).thenReturn(transactions);

                // when
                TransactionListRespDto transactionListRespDto = transactionService.입출금목록보기(userId, accountId, gubun,
                                page);
                String body = om.writeValueAsString(transactionListRespDto);
                log.debug("디버그 : " + body);

                // then
                assertThat(transactionListRespDto.getTransactions().size()).isEqualTo(2);
                assertThat(transactionListRespDto.getTransactions().get(0).getBalance()).isEqualTo(800L);
        }
}
