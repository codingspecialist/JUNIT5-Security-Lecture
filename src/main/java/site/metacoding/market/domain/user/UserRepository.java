package site.metacoding.market.domain.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer>, UserDao {
    @Query(value = "select u from User u where u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);
}
