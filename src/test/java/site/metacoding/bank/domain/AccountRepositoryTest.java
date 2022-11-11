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
import org.springframework.test.context.ActiveProfiles;

import site.metacoding.bank.bean.DummyBeans;
import site.metacoding.bank.config.enums.ResponseEnum;
import site.metacoding.bank.config.exceptions.CustomApiException;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.domain.user.UserRepository;

@ActiveProfiles("test")
@DataJpaTest // 내부에 Transactional 어노테이션 있어서 자동 롤백됨.
public class AccountRepositoryTest extends DummyBeans {
        private final Logger log = LoggerFactory.getLogger(getClass());
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private AccountRepository accountRepository;
        @Autowired
        private EntityManager em;

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
                User ssarUser = userRepository.save(newUser(1L, "ssar"));
                User cosUser = userRepository.save(newUser(2L, "cos"));
                User adminUser = userRepository.save(newUser(3L, "admin"));
                Account ssarAccount1 = accountRepository.save(newAccount(1L, 1111L, ssarUser));
                Account ssarAccount2 = accountRepository.save(newAccount(2L, 2222L, ssarUser));
                Account cosAccount1 = accountRepository.save(newAccount(3L, 3333L, cosUser));
        }
}
