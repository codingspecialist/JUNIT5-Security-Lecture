package site.metacoding.bank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import site.metacoding.bank.config.enums.UserEnum;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountAllRespDto;
import site.metacoding.bank.dto.account.AccountRespDto.AccountSaveRespDto;

/*
 * Mock -> 진짜 객체를 추상화된 가짜 객체로 만들어서 Mockito환경에 주입함.
 * InjectMocks -> Mock된 가짜 객체를 진짜 객체 UserService를 만들어서 주입함
 * MockBean -> Mock객체들을 스프링 ApplicationContext에 주입함. (IoC컨테이너 주입)
 * Spy -> 진짜 객체를 만들어서 Mockito환경에 주입함.
 * SpyBean -> Spay객체들을 스프링 ApplicationContext에 주입함. (IoC컨테이너 주입)
 */

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
        private final Logger log = LoggerFactory.getLogger(getClass());

        @InjectMocks
        private AccountService accountService;

        @Mock
        private AccountRepository accountRepository;

        @Spy
        private BCryptPasswordEncoder passwordEncoder;

        @Test
        public void 계좌등록하기_test() throws Exception {
                // given
                AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
                accountSaveReqDto.setNumber(1111L);
                accountSaveReqDto.setPassword("1234");
                User user = User.builder()
                                .id(1L)
                                .username("ssar")
                                .password("1234")
                                .email("ssar@nate.com")
                                .role(UserEnum.CUSTOMER)
                                .build();

                // stub (가정)
                Account accountPS = Account.builder()
                                .id(1L)
                                .number(1111L)
                                .password("1234")
                                .balance(0L)
                                .user(user)
                                .build();
                when(accountRepository.save(any())).thenReturn(accountPS);

                // when
                AccountSaveRespDto accountSaveRespDto = accountService.계좌등록하기(accountSaveReqDto, user);

                // then
                assertThat(accountSaveRespDto.getId()).isEqualTo(1L);
                assertThat(accountSaveRespDto.getNumber()).isEqualTo(1111L);
                assertThat(accountSaveRespDto.getPassword()).isEqualTo("1234");
                assertThat(accountSaveRespDto.getBalance()).isEqualTo(0L);
        }

        @Test
        public void 계좌목록보기_유저별_test() throws Exception {
                // given
                Long id = 1L;

                // stub
                String encPassword = passwordEncoder.encode("1234");
                User customer1 = User.builder().username("ssar").password(encPassword).email("ssar@nate.com")
                                .role(UserEnum.CUSTOMER)
                                .build();

                Account account1 = Account.builder()
                                .number(1111L)
                                .password("1234")
                                .balance(100000L)
                                .user(customer1)
                                .build();

                Account account2 = Account.builder()
                                .number(2222L)
                                .password("1234")
                                .balance(100000L)
                                .user(customer1)
                                .build();

                List<Account> accounts = Arrays.asList(account1, account2);
                when(accountRepository.findByUserId(id)).thenReturn(accounts);

                // when
                List<AccountAllRespDto> accountAllRespDtos = accountService.계좌목록보기_유저별(id);

                // then
                assertThat(accountAllRespDtos.get(0).getNumber()).isEqualTo(1111L);
                assertThat(accountAllRespDtos.get(1).getNumber()).isEqualTo(2222L);
        }
}
