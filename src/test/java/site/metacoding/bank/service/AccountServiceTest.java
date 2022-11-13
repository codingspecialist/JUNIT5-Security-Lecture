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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import site.metacoding.bank.beans.DummyMockBeans;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.transaction.TransactionRepository;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.dto.account.AccountReqDto.AccountDeleteReqDto;
import site.metacoding.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountAllRespDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountDeleteRespDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountDetailRespDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountSaveRespDto;

/*
 * Mock -> 진짜 객체를 추상화된 가짜 객체로 만들어서 Mockito환경에 주입함.
 * InjectMocks -> Mock된 가짜 객체를 진짜 객체 UserService를 만들어서 주입함
 * MockBean -> Mock객체들을 스프링 ApplicationContext에 주입함. (IoC컨테이너 주입)
 * Spy -> 진짜 객체를 만들어서 Mockito환경에 주입함.
 * SpyBean -> Spay객체들을 스프링 ApplicationContext에 주입함. (IoC컨테이너 주입)
 */

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest extends DummyMockBeans {
        private final Logger log = LoggerFactory.getLogger(getClass());

        @InjectMocks
        private AccountService accountService;

        @Mock
        private AccountRepository accountRepository;

        @Mock
        private TransactionRepository transactionRepository;

        @Spy
        private BCryptPasswordEncoder passwordEncoder;

        @Test
        public void 계좌등록하기_test() throws Exception {
                // given
                AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
                accountSaveReqDto.setNumber(1111L);
                accountSaveReqDto.setPassword("1234");
                accountSaveReqDto.setOwnerName("쌀");

                // stub
                User ssarUser = newUser(1L, "ssar");
                Account ssarAccount = newAccount(1L, 1111L, "쌀", ssarUser);

                when(accountRepository.save(any())).thenReturn(ssarAccount);

                // when
                AccountSaveRespDto accountSaveRespDto = accountService.계좌등록하기(accountSaveReqDto, ssarUser);
                String body = om.writeValueAsString(accountSaveRespDto);
                log.debug("디버그 : " + body);

                // then
                assertThat(accountSaveRespDto.getId()).isEqualTo(1L);
                assertThat(accountSaveRespDto.getNumber()).isEqualTo(1111L);
                assertThat(accountSaveRespDto.getBalance()).isEqualTo(1000L);
        }

        @Test
        public void 계좌목록보기_유저별_test() throws Exception {
                // given
                Long id = 1L;

                // stub
                User ssarUser = newUser(1L, "ssar");
                User cosUser = newUser(2L, "cos");
                Account ssarAccount1 = newAccount(1L, 1111L, "쌀", ssarUser);
                Account ssarAccount2 = newAccount(2L, 2222L, "쌀", ssarUser);
                Account cosAccount1 = newAccount(3L, 3333L, "코스", cosUser);
                List<Account> accounts = Arrays.asList(ssarAccount1, ssarAccount2, cosAccount1);

                when(accountRepository.findByActiveUserId(any())).thenReturn(accounts);

                // when
                List<AccountAllRespDto> accountAllRespDtos = accountService.계좌목록보기_유저별(id);
                String body = om.writeValueAsString(accountAllRespDtos);
                log.debug("디버그 : " + body);

                // then
                assertThat(accountAllRespDtos.get(0).getNumber()).isEqualTo(1111L);
                assertThat(accountAllRespDtos.get(1).getNumber()).isEqualTo(2222L);
        }

        @Test
        public void 계좌상세보기_test() throws Exception {
                // given
                Long accountId = 1L;

                // stub (select는 stub을 나눌 필요가 없다.)
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
                List<Transaction> transactions = Arrays.asList(withdrawTransaction1, withdrawTransaction2,
                                depositTransaction1, transferTransaction1, transferTransaction2);

                when(accountRepository.findById(any())).thenReturn(Optional.of(ssarAccount1));
                when(transactionRepository.findByTransactionHistory(any(), any()))
                                .thenReturn(transactions);

                // when
                AccountDetailRespDto accountDetailRespDto = accountService.계좌상세보기(accountId);
                String body = om.writeValueAsString(accountDetailRespDto);
                log.debug("디버그 : " + body);

                // then
                assertThat(accountDetailRespDto.getId()).isEqualTo(1L);
                assertThat(accountDetailRespDto.getNumber()).isEqualTo(1111L);
        }

        @Test
        public void 계좌삭제하기_test() throws Exception {
                // given
                Long accountId = 1L;
                AccountDeleteReqDto accountDeleteReqDto = new AccountDeleteReqDto();
                accountDeleteReqDto.setAccountPassword("1234");

                // stub
                User ssarUser = newUser(1L, "ssar");
                Account ssarAccount = newAccount(1L, 1111L, "쌀", ssarUser);
                when(accountRepository.findById(any())).thenReturn(Optional.of(ssarAccount));

                // when
                AccountDeleteRespDto accountDeleteRespDto = accountService.계좌삭제(accountDeleteReqDto, accountId);
                String body = om.writeValueAsString(accountDeleteRespDto);
                log.debug("디버그 : " + body);

                // then
                assertThat(accountDeleteRespDto.getIsUse()).isEqualTo(false);
        }
}
