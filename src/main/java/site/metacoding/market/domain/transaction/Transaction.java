package site.metacoding.market.domain.transaction;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.metacoding.market.domain.AudingTime;
import site.metacoding.market.domain.account.Account;
import site.metacoding.market.enums.TransactionEnum;

/**
 * deposit and withdrawal history
 * 출금계좌 -> 입금계좌 (이체)
 * 출금계좌(null) -> 입금계좌 (자동화기기)
 * 출금계좌 -> 입금계좌(null) (자동화기기)
 */
@NoArgsConstructor
@Getter
@Table(name = "transaction")
@Entity
public class Transaction extends AudingTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account withdrawAccount; // 출금 계좌

    @ManyToOne(fetch = FetchType.LAZY)
    private Account depositAccount; // 입금 계좌

    private Long amount; // 입금액, 출금액

    @Enumerated(EnumType.STRING)
    private TransactionEnum gubun; // 이체, 자동화기기

    @Builder
    public Transaction(Long id, Account withdrawAccount, Account depositAccount, Long amount, TransactionEnum gubun) {
        this.id = id;
        this.withdrawAccount = withdrawAccount;
        this.depositAccount = depositAccount;
        this.amount = amount;
        this.gubun = gubun;
    }

}
