package site.metacoding.bank.config.auth;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.domain.user.UserRepository;

@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent() ? new LoginUser(user.get()) : null;
    }
}
