package site.metacoding.bank.domain.transaction;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.metacoding.bank.enums.ResponseEnum;
import site.metacoding.bank.enums.TransactionEnum;
import site.metacoding.bank.handler.exception.CustomApiException;

interface Dao {
    // List<Transaction> findByTransactionHistory(@Param("withdrawAccountId") Long
    // withdrawAccountId,
    // @Param("depositAccountId") Long depositAccountId);

    List<Transaction> findByTransactionHistory(Long accountId, String gubun);
}

@Slf4j
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements Dao {
    private final EntityManager em;

    public List<Transaction> findByTransactionHistory(Long accountId, String gubun) {
        String sql = "";
        sql += "select t from Transaction t ";
        sql += "left join t.withdrawAccount wa ";
        sql += "left join t.depositAccount da ";

        if (gubun == null || gubun.isEmpty()) {
            sql += "where t.withdrawAccount.id = :withdrawAccountId ";
            sql += "or ";
            sql += "t.depositAccount.id = :depositAccountId";
        } else if (TransactionEnum.valueOf(gubun) == TransactionEnum.DEPOSIT) {
            sql += "where t.depositAccount.id = :depositAccountId";
        } else if (TransactionEnum.valueOf(gubun) == TransactionEnum.WITHDRAW) {
            sql += "where t.withdrawAccount.id = :withdrawAccountId";
        } else {
            throw new CustomApiException(ResponseEnum.BAD_REQUEST);
        }

        TypedQuery<Transaction> query = em.createQuery(sql, Transaction.class);

        if (gubun == null || gubun.isEmpty()) {
            query = query.setParameter("withdrawAccountId", accountId);
            query = query.setParameter("depositAccountId", accountId);
        } else if (TransactionEnum.valueOf(gubun) == TransactionEnum.DEPOSIT) {
            query = query.setParameter("depositAccountId", accountId);
        } else if (TransactionEnum.valueOf(gubun) == TransactionEnum.WITHDRAW) {
            query = query.setParameter("withdrawAccountId", accountId);
        }

        return query.getResultList();
    }
}
