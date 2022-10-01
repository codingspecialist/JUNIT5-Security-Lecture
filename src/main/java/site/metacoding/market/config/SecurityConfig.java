package site.metacoding.market.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import site.metacoding.market.handler.LoginHandler;

@Configuration
public class SecurityConfig {

    @Autowired
    private LoginHandler loginHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();
        http.csrf().disable();

        http.authorizeRequests()
                .antMatchers("/secure/**").authenticated()
                .antMatchers("/buyer/**").access("hasRole('ROLE_BUYER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/seller/**").access("hasRole('ROLE_SELLER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginProcessingUrl("/api/login")
                .successHandler(loginHandler)
                .failureHandler(loginHandler);
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
