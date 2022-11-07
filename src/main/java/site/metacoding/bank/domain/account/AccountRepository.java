package site.metacoding.bank.domain.account;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("select ac from Account ac join fetch ac.user u WHERE ac.id = :id")
    Optional<Account> findById(@Param("id") Long id);

    List<Account> findByUserId(@Param("userId") Long userId);

}
