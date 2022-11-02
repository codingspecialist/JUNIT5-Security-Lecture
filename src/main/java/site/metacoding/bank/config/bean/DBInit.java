package site.metacoding.bank.config.bean;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.RequiredArgsConstructor;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.domain.user.UserRepository;
import site.metacoding.bank.enums.UserEnum;

@RequiredArgsConstructor
@Configuration
public class DBInit {

    private final BCryptPasswordEncoder encoder;

    // ADMIN 유저 하나 생성해두기
    @Profile("dev")
    @Bean
    public CommandLineRunner demo(UserRepository userRepository) {
        String password = encoder.encode("1234");

        return (args) -> {
            User admin = User.builder().username("admin").password(password).email("admin@nate.com")
                    .role(UserEnum.ADMIN)
                    .build();
            userRepository.save(admin);
            // User ssar =
            // User.builder().username("ssar").password(password).email("ssar@nate.com")
            // .role(UserEnum.CUSTOMER)
            // .build();
            // userRepository.save(ssar);
        };
    }
}
