package site.metacoding.bank.domain.transaction;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import lombok.RequiredArgsConstructor;
import site.metacoding.bank.config.enums.ResponseEnum;
import site.metacoding.bank.config.enums.TransactionEnum;
import site.metacoding.bank.config.exceptions.CustomApiException;

interface Dao {
    List<Transaction> findByAccountId(Long accountId, String gubun, Integer page);
}

@RequiredArgsConstructor
public class TransactionRepositoryImpl implements Dao {
    private final EntityManager em;

    public List<Transaction> findByAccountId(Long accountId, String gubun, Integer page) {
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

        Integer firstNum = page * 3;
        query.setFirstResult(firstNum);
        query.setMaxResults(3);
        return query.getResultList();
    }
}
