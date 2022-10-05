package site.metacoding.market.domain.user;

import java.util.Optional;

import javax.persistence.EntityManager;

import lombok.RequiredArgsConstructor;

interface Dao {
    public Optional<User> findByUsername(String username);
}

@RequiredArgsConstructor
public class UserDao implements Dao {
    private final EntityManager em;

    public Optional<User> findByUsername(String username) {
        return Optional.of(em.createQuery("SELECT u FROM user u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult());

    }
}
