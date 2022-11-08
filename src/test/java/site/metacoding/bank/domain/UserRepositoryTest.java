package site.metacoding.bank.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import site.metacoding.bank.config.enums.ResponseEnum;
import site.metacoding.bank.config.enums.UserEnum;
import site.metacoding.bank.config.exceptions.CustomApiException;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.domain.user.UserRepository;

@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@DataJpaTest // 내부에 @Transactional 존재
public class UserRepositoryTest {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    public void setUp() {
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

    public void dataSetting() {
        String encPassword = passwordEncoder.encode("1234");
        User customer1 = User.builder().username("ssar").password(encPassword).email("ssar@nate.com")
                .role(UserEnum.CUSTOMER)
                .build();
        userRepository.save(customer1);
        log.debug("디버그 : id:1, username: ssar 유저 생성");

        User customer2 = User.builder().username("cos").password(encPassword).email("cos@nate.com")
                .role(UserEnum.CUSTOMER)
                .build();
        userRepository.save(customer2);
        log.debug("디버그 : id:2, username: cos 유저 생성");

        User admin = User.builder().username("admin").password(encPassword).email("admin@nate.com")
                .role(UserEnum.ADMIN)
                .build();
        userRepository.save(admin);
        log.debug("디버그 : id:3, username: admin 관리자 생성");
    }
}
