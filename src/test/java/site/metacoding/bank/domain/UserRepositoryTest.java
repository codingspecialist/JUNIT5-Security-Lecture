package site.metacoding.bank.domain;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import site.metacoding.bank.beans.DummyBeans;
import site.metacoding.bank.config.enums.ResponseEnum;
import site.metacoding.bank.config.exceptions.CustomApiException;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.domain.user.UserRepository;

@ActiveProfiles("test")
@DataJpaTest // 내부에 @Transactional 존재
public class UserRepositoryTest extends DummyBeans {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        autoincrementReset();
        dataSetting();
    }

    @Test
    public void findByUsername_test() throws Exception {
        // given
        String username = "ssar";

        // when
        User userPS = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomApiException(ResponseEnum.BAD_REQUEST));

        // then
        assertThat(userPS.getUsername()).isEqualTo(username);
    }

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
        User ssarUser = userRepository.save(newUser("ssar"));
        User cosUser = userRepository.save(newUser("cos"));
        User adminUser = userRepository.save(newUser("admin"));
    }
}
