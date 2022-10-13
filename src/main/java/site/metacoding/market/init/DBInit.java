package site.metacoding.market.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.RequiredArgsConstructor;
import site.metacoding.market.domain.user.User;
import site.metacoding.market.domain.user.UserRepository;

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
            User user = User.create("ssar", encPassword, "getinthere@naver.com", "ADMIN");
            userRepository.save(user);
        };
    }
}
