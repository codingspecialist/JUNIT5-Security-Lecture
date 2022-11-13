package site.metacoding.bank.domain.transaction;

import static site.metacoding.bank.domain.transaction.QTransaction.transaction;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import site.metacoding.bank.config.enums.TransactionEnum;

interface Dao {
    List<Transaction> findByAccountId(Long accountId, String gubun, Integer page);

    List<Transaction> findByAccountIdQueryDSL(Long accountId, String gubun, Integer page);
}

@RequiredArgsConstructor
public class TransactionRepositoryImpl implements Dao {
    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;

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

        query.setFirstResult(page * 3);
        query.setMaxResults(3);
        return query.getResultList();
    }

    public List<Transaction> findByAccountIdQueryDSL(Long accountId, String gubun, Integer page) {
        // select
        JPAQuery<Transaction> query = jpaQueryFactory.selectFrom(transaction);

        // join
        query.leftJoin(transaction.withdrawAccount).leftJoin(transaction.depositAccount);

        // where
        query.where(gubunCheck(gubun, accountId));

        // paging
        query.limit(3).offset(page * 3);

        return query.fetch();
    }

    private BooleanExpression gubunCheck(String gubun, Long accountId) {
        if (!StringUtils.hasText(gubun)) {
            return transaction.withdrawAccount.id.eq(accountId).or(transaction.depositAccount.id.eq(accountId));
        } else if (TransactionEnum.valueOf(gubun) == TransactionEnum.DEPOSIT) {
            return transaction.depositAccount.id.eq(accountId);
        } else if (TransactionEnum.valueOf(gubun) == TransactionEnum.WITHDRAW) {
            return transaction.withdrawAccount.id.eq(accountId);
        } else {
            return null;
        }
    }
}
