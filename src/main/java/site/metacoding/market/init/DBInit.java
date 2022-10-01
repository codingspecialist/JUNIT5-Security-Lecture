package site.metacoding.market.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.RequiredArgsConstructor;
import site.metacoding.market.domain.user.User;
import site.metacoding.market.domain.user.UserRepository;
import site.metacoding.market.enums.RoleEnum;

@RequiredArgsConstructor
@Configuration
public class DBInit {

    private final BCryptPasswordEncoder encoder;

    @Profile("dev")
    @Bean
    public CommandLineRunner demo(UserRepository userRepository) {
        String rawPassword = "1234";
        String encPassword = encoder.encode(rawPassword);

        return (args) -> {
            User principal = User.builder()
                    .username("ssar")
                    .password(encPassword)
                    .email("getinthere@naver.com")
                    .role(RoleEnum.ADMIN)
                    .build();

            MyLog.info("나실행됨?");
            userRepository.save(principal);
        };
    }
}
