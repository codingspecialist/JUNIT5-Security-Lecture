package site.metacoding.bank.config.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BCryptPasswordEncoderBean {
    @Bean
    public BCryptPasswordEncoderBean bCryptPasswordEncoder() {
        return new BCryptPasswordEncoderBean();
    }
}
