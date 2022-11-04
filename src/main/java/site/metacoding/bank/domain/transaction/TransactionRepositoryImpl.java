package site.metacoding.bank.domain.transaction;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.repository.query.Param;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

interface Dao {
    // List<Transaction> findByTransactionHistory(@Param("withdrawAccountId") Long
    // withdrawAccountId,
    // @Param("depositAccountId") Long depositAccountId);

    List<Transaction> findByTransactionHistory(Long withdrawAccountId, Long depositAccountId);
}

@Slf4j
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements Dao {
    private final EntityManager em;

    public List<Transaction> findByTransactionHistory(Long withdrawAccountId, Long depositAccountId) {
        log.debug("디버그 : 11111111111111111111111111111111111111111111");
        String sql = "";
        sql += "select t from Transaction t ";
        sql += "left join t.withdrawAccount wa ";
        sql += "left join t.depositAccount da ";
        sql += "where t.withdrawAccount.id = :withdrawAccountId ";
        sql += "or ";
        sql += "t.depositAccount.id = :depositAccountId";

        return em.createQuery(sql, Transaction.class)
                .setParameter("withdrawAccountId", withdrawAccountId)
                .setParameter("depositAccountId", depositAccountId)
                .getResultList();
    }
}
