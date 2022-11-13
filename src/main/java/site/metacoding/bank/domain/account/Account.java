package site.metacoding.bank.domain.account;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.context.annotation.Profile;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.metacoding.bank.config.enums.ResponseEnum;
import site.metacoding.bank.config.enums.TransactionEnum;
import site.metacoding.bank.config.exceptions.CustomApiException;
import site.metacoding.bank.domain.AudingTime;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.user.User;

/**
 * 계좌
 */

@NoArgsConstructor
@Getter
@Table(name = "account")
@Entity
public class Account extends AudingTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long number; // 계좌 번호

    @Column(nullable = false)
    private String ownerName; // 계좌주 실명

    @Column(nullable = false)
    private String password; // 계좌 비밀번호

    @Column(nullable = false)
    private Long balance; // 잔액

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private Boolean isUse;

    @OneToMany(mappedBy = "withdrawAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Transaction> withdrawTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "depositAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Transaction> depositTransactions = new ArrayList<>();

    public void addWithdrawTransaction(Transaction transaction) {
        this.withdrawTransactions.add(transaction);
    }

    public void addDepositTransaction(Transaction transaction) {
        this.depositTransactions.add(transaction);
    }

    @Builder
    public Account(Long id, Long number, String ownerName, String password, Long balance, User user, Boolean isUse) {
        this.id = id;
        this.number = number;
        this.ownerName = ownerName;
        this.password = password;
        this.balance = balance;
        this.user = user;
        this.isUse = isUse;
    }

    @Profile("test")
    public void setMockData(Long id) {
        this.id = id;
        super.createdAt = LocalDateTime.now();
        super.updatedAt = LocalDateTime.now();
    }

    /*
     * 잔액 유효성 검사
     */
    public void checkBalance(Long amount) {
        if (amount > balance) {
            throw new CustomApiException(ResponseEnum.LACK_BALANCE);
        }
    }

    /*
     * 출금
     */
    public Transaction withdraw(Long amount) {
        checkBalance(amount);
        balance = balance - amount;
        Transaction transaction = Transaction.builder()
                .withdrawAccount(this)
                .withdrawAccountBalance(balance)
                .amount(amount)
                .gubun(TransactionEnum.WITHDRAW)
                .build();
        addWithdrawTransaction(transaction);
        return transaction;
    }

    /*
     * 입금
     */
    public Transaction deposit(Long amount) {
        balance = balance + amount;
        Transaction transaction = Transaction.builder()
                .depositAccount(this)
                .depositAccountBalance(balance)
                .amount(amount)
                .gubun(TransactionEnum.DEPOSIT)
                .build();
        addWithdrawTransaction(transaction);
        return transaction;
    }

    /*
     * 이체
     */
    public Transaction transper(Long amount, Account deposiAccount) {
        checkBalance(amount);
        withdraw(amount);
        deposiAccount.deposit(amount);
        Transaction transaction = Transaction.builder()
                .withdrawAccount(this)
                .depositAccount(deposiAccount)
                .withdrawAccountBalance(balance)
                .depositAccountBalance(deposiAccount.getBalance())
                .amount(amount)
                .gubun(TransactionEnum.TRANSFER)
                .build();
        addWithdrawTransaction(transaction);
        return transaction;
    }

    /*
     * 계좌 소유자 확인
     */
    public void ownerCheck(Long userId) {
        if (user.getId() != userId) {
            throw new CustomApiException(ResponseEnum.ISNOT_ACCOUNT_OWNER);
        }
    }

    /*
     * 계좌 비밀번호 확인
     */
    public void passwordCheck(String password) {
        if (!this.password.equals(password)) {
            throw new CustomApiException(ResponseEnum.ISNOT_SAME_PASSWORD);
        }
    }

    /*
     * 입출금이체 0원 체크
     */
    public void zeroAmountCheck(Long amount) {
        if (amount <= 0L) {
            throw new CustomApiException(ResponseEnum.ISNOT_ZERO_AMOUNT);
        }
    }

    public Boolean delete() {
        isUse = false;
        return isUse;
    }

}
