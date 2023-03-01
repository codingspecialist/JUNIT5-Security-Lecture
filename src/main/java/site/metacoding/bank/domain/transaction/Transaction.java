package site.metacoding.bank.domain.transaction;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.context.annotation.Profile;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.metacoding.bank.config.enums.TransactionEnum;
import site.metacoding.bank.domain.AudingTime;
import site.metacoding.bank.domain.account.Account;

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
    private Account withdrawAccount; // 출금 계좌 (보내는 분)

    @ManyToOne(fetch = FetchType.LAZY)
    private Account depositAccount; // 입금 계좌 (받는 분)

    @Column(nullable = false)
    private Long amount; // 입출금, 이체 금액
    private Long withdrawAccountBalance; // 출금계좌 현재 잔액
    private Long depositAccountBalance; // 입금계좌 현재 잔액

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionEnum gubun; // 입금(자동화기기로부터), 출금(자동화기기로), 이체(다른계좌)

    @Builder
    public Transaction(Long id, Account withdrawAccount, Account depositAccount, Long amount,
            Long withdrawAccountBalance, Long depositAccountBalance, TransactionEnum gubun) {
        this.id = id;
        this.withdrawAccount = withdrawAccount;
        this.depositAccount = depositAccount;
        this.amount = amount;
        this.withdrawAccountBalance = withdrawAccountBalance;
        this.depositAccountBalance = depositAccountBalance;
        this.gubun = gubun;

    }

    @Profile("test")
    public void setMockData(Long id) {
        this.id = id;
        super.createdAt = LocalDateTime.now();
        super.updatedAt = LocalDateTime.now();
    }

}
