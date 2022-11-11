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

import com.fasterxml.jackson.databind.ObjectMapper;

import site.metacoding.bank.bean.DummyBeans;
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
public class AccountServiceTest extends DummyBeans {
        private final Logger log = LoggerFactory.getLogger(getClass());

        @InjectMocks
        private AccountService accountService;

        @Mock
        private AccountRepository accountRepository;

        @Mock
        private TransactionRepository transactionRepository;

        @Spy
        private BCryptPasswordEncoder passwordEncoder;

        @Spy
        private ObjectMapper om;

        @Test
        public void 계좌삭제하기_test() {
                // given
                Long accountId = 1L;
                AccountDeleteReqDto accountDeleteReqDto = new AccountDeleteReqDto();
                accountDeleteReqDto.setAccountPassword("1234");

                // stub
                User ssarUser = newUser(1L, "ssar");
                Account ssarAccount1 = newAccount(1L, 1111L, "쌀", ssarUser);
                when(accountRepository.findById(any())).thenReturn(Optional.of(ssarAccount1));

                // when
                AccountDeleteRespDto accountDeleteRespDto = accountService.계좌삭제(accountDeleteReqDto, accountId);

                // then
                assertThat(accountDeleteRespDto.getIsUse()).isEqualTo(false);
        }

        @Test
        public void 계좌등록하기_test() throws Exception {
                // given
                AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
                accountSaveReqDto.setNumber(1111L);
                accountSaveReqDto.setPassword("1234");
                accountSaveReqDto.setOwnerName("쌀");

                // stub
                User ssarUser = newUser(1L, "ssar");
                Account ssarAccount1 = newAccount(1L, 1111L, "쌀", ssarUser);

                when(accountRepository.save(any())).thenReturn(ssarAccount1);

                // when
                AccountSaveRespDto accountSaveRespDto = accountService.계좌등록하기(accountSaveReqDto, ssarUser);
                String body = om.writeValueAsString(accountSaveRespDto);
                log.debug("디버그 : " + body);

                // then
                assertThat(accountSaveRespDto.getId()).isEqualTo(ssarAccount1.getId());
                assertThat(accountSaveRespDto.getNumber()).isEqualTo(ssarAccount1.getNumber());
                assertThat(accountSaveRespDto.getBalance()).isEqualTo(ssarAccount1.getBalance());
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

                when(accountRepository.findByUserId(any())).thenReturn(accounts);

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

                // stub
                User ssarUser = newUser(1L, "ssar");
                User cosUser = newUser(2L, "cos");
                Account ssarAccount1 = newAccount(1L, 1111L, "쌀", ssarUser);
                Account cosAccount1 = newAccount(3L, 3333L, "코스", cosUser);
                Transaction withdrawTransaction1 = newWithdrawTransaction(1L, ssarAccount1);
                Transaction withdrawTransaction2 = newWithdrawTransaction(2L, ssarAccount1);
                Transaction depositTransaction1 = newDepositTransaction(3L, ssarAccount1);
                Transaction transferTransaction1 = newTransferTransaction(4L, ssarAccount1, cosAccount1);
                List<Transaction> transactions = Arrays.asList(withdrawTransaction1, withdrawTransaction2,
                                depositTransaction1, transferTransaction1);

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

        // 계좌삭제 테스트 할 것이 없음. (DTO 잘 변경됐는지 확인이라고 해야하는데, 그냥 삭제만 함. )
        // 삭제는 Repository 테스트에서 이미 확인함.

}
