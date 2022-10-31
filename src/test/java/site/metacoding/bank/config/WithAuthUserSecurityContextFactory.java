package site.metacoding.bank.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithAuthUserSecurityContextFactory implements WithSecurityContextFactory<WithAuthUser> {

    @Override
    public SecurityContext createSecurityContext(WithAuthUser annotation) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        // User user = User.builder()
        // .id(1L)
        // .username("ssar")
        // .password(encPassword)
        // .email("ssar@nate.com")
        // .role(UserEnum.CUSTOMER)
        // .build();

        // LoginUser loginUser = new LoginUser(user);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("ssar", encPassword);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
        return context;
    }
}