package site.metacoding.market.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.RequiredArgsConstructor;
import site.metacoding.market.domain.user.User;
import site.metacoding.market.domain.user.UserRepository;
import site.metacoding.market.enums.UserEnum;

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
            User user = User.builder().username("admin").password(password).email("admin@nate.com").role(UserEnum.ADMIN)
                    .build();
            userRepository.save(user);
        };
    }
}
