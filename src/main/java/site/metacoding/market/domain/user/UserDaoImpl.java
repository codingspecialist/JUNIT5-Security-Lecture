package site.metacoding.market.domain.user;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.metacoding.market.util.LogPrefix;

interface UserDao {
    public Optional<User> findByUsername(String username);
}

@Slf4j
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {
    private final EntityManager em;

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {

        log.debug(LogPrefix.TAG + "findByUsername()");
        return Optional.of(em.createQuery("SELECT u FROM User u WHERE u.username=:username", User.class)
                .setParameter("username", username)
                .getSingleResult());

    }
}
