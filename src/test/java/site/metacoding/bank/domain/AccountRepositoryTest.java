package site.metacoding.bank.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import site.metacoding.bank.config.enums.ResponseEnum;
import site.metacoding.bank.config.enums.UserEnum;
import site.metacoding.bank.config.exceptions.CustomApiException;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.domain.user.UserRepository;

@ActiveProfiles("test")
@DataJpaTest // 내부에 Transactional 어노테이션 있어서 자동 롤백됨.
public class AccountRepositoryTest {
        private final Logger log = LoggerFactory.getLogger(getClass());
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private AccountRepository accountRepository;
        @Autowired
        private EntityManager em;

        private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        @BeforeEach
        public void setUp() {
                autoincrementReset();
                dataSetting();
        }

        @Test
        public void findById_test() throws Exception {
                // given
                Long id = 1L;

                // when
                Account accountPS = accountRepository.findById(id).orElseThrow(
                                () -> new CustomApiException(ResponseEnum.BAD_REQUEST));

                // then
                assertThat(accountPS.getNumber()).isEqualTo(1111L);
        }

        @Test
        public void findByUserId_test() throws Exception {
                // given
                Long userId = 1L;

                // when
                List<Account> accountsPS = accountRepository.findByUserId(userId);

                // then
                assertThat(accountsPS.size()).isEqualTo(2);
                assertThat(accountsPS.get(0).getUser().getUsername()).isEqualTo("ssar");
        }

        // @DataJpaTest에서는 truncate를 해도 auto_increment가 초기화 되지 않아서 아래 방법 사용
        private void autoincrementReset() {
                this.em
                                .createNativeQuery("ALTER TABLE transaction ALTER COLUMN `id` RESTART WITH 1")
                                .executeUpdate();
                this.em
                                .createNativeQuery("ALTER TABLE account ALTER COLUMN `id` RESTART WITH 1")
                                .executeUpdate();
                this.em
                                .createNativeQuery("ALTER TABLE users ALTER COLUMN `id` RESTART WITH 1")
                                .executeUpdate();
        }

        public void dataSetting() {
                String encPassword = passwordEncoder.encode("1234");
                User customer1 = User.builder().username("ssar").password(encPassword).email("ssar@nate.com")
                                .role(UserEnum.CUSTOMER)
                                .build();
                User customer1PS = userRepository.save(customer1);
                log.debug("디버그 : id:1, username: ssar 유저 생성");
                log.debug("디버그 : " + customer1PS.getId());

                User customer2 = User.builder().username("cos").password(encPassword).email("cos@nate.com")
                                .role(UserEnum.CUSTOMER)
                                .build();
                User customer2PS = userRepository.save(customer2);
                log.debug("디버그 : id:2, username: cos 유저 생성");
                log.debug("디버그 : " + customer2PS.getId());

                User admin = User.builder().username("admin").password(encPassword).email("admin@nate.com")
                                .role(UserEnum.ADMIN)
                                .build();
                User adminPS = userRepository.save(admin);
                log.debug("디버그 : id:3, username: admin 관리자 생성");
                log.debug("디버그 : " + adminPS.getId());

                Account account1 = Account.builder()
                                .number(1111L)
                                .password("1234")
                                .balance(100000L)
                                .user(customer1PS)
                                .build();
                accountRepository.save(account1);
                log.debug("디버그 : ssar 고객 1111 계좌 생성 , 잔액 100000");

                Account account2 = Account.builder()
                                .number(2222L)
                                .password("1234")
                                .balance(100000L)
                                .user(customer1PS)
                                .build();
                accountRepository.save(account2);
                log.debug("디버그 : ssar 고객 2222 계좌 생성 , 잔액 100000");

                Account account3 = Account.builder()
                                .number(3333L)
                                .password("1234")
                                .balance(100000L)
                                .user(customer2PS)
                                .build();
                accountRepository.save(account3);
                log.debug("디버그 : cos 고객 3333 계좌 생성 , 잔액 100000");
        }
}
