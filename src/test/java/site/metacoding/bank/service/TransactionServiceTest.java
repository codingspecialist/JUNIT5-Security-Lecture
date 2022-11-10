package site.metacoding.bank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;

import site.metacoding.bank.config.enums.TransactionEnum;
import site.metacoding.bank.config.enums.UserEnum;
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
public class TransactionServiceTest {
        private final Logger log = LoggerFactory.getLogger(getClass());

        @InjectMocks
        private TransactionService transactionService;

        @Mock
        private AccountRepository accountRepository;

        @Mock
        private TransactionRepository transactionRepository;

        @Spy
        private BCryptPasswordEncoder passwordEncoder;

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
                User ssar = User.builder()
                                .id(1L)
                                .username("ssar")
                                .password("1234")
                                .email("ssar@nate.com")
                                .role(UserEnum.CUSTOMER)
                                .build();
                Account ssarAccount = Account.builder()
                                .id(1L)
                                .number(1111L)
                                .password("1234")
                                .balance(0L)
                                .user(ssar)
                                .build();
                when(accountRepository.findById(any()))
                                .thenReturn(Optional.of(ssarAccount));

                Transaction transaction = Transaction.builder()
                                .id(1L)
                                .depositAccount(ssarAccount)
                                .amount(depositReqDto.getAmount())
                                .depositAccountBalance(ssarAccount.getBalance() - depositReqDto.getAmount())
                                .gubun(TransactionEnum.valueOf(depositReqDto.getGubun()))
                                .build();
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
                withdrawReqDto.setAmount(1000L); // 잔액부족 여부 확인 -> 3000원 변경
                withdrawReqDto.setGubun("WITHDRAW"); // 구분값 검증 -> DEPOSIT 변경

                // stub
                User ssar = User.builder()
                                .id(1L)
                                .username("ssar")
                                .password("1234")
                                .email("ssar@nate.com")
                                .role(UserEnum.CUSTOMER)
                                .build();
                Account ssarAccount = Account.builder()
                                .id(1L)
                                .number(1111L)
                                .password("1234")
                                .balance(2000L)
                                .user(ssar)
                                .build();
                User cos = User.builder()
                                .id(2L)
                                .username("cos")
                                .password("1234")
                                .email("cos@nate.com")
                                .role(UserEnum.CUSTOMER)
                                .build();
                Account cosAccount = Account.builder()
                                .id(2L)
                                .number(2222L)
                                .password("1234")
                                .balance(0L)
                                .user(cos)
                                .build();

                when(accountRepository.findById(any()))
                                .thenReturn(Optional.of(ssarAccount));

                Transaction transaction = Transaction.builder()
                                .id(1L)
                                .withdrawAccount(ssarAccount)
                                .depositAccount(cosAccount)
                                .amount(withdrawReqDto.getAmount()) // 1000원
                                .withdrawAccountBalance(ssarAccount.getBalance() - withdrawReqDto.getAmount())
                                .gubun(TransactionEnum.valueOf(withdrawReqDto.getGubun()))
                                .build();

                when(transactionRepository.save(any()))
                                .thenReturn(transaction);

                // when
                WithdrawRespDto withdrawRespDto = transactionService.출금하기(withdrawReqDto, userId);
                String body = om.writeValueAsString(withdrawRespDto);
                log.debug("디버그 : " + body);

                // then (잔액 확인)
                assertThat(withdrawRespDto.getWithdrawAccount().getBalance()).isEqualTo(ssarAccount.getBalance());
        }
}
