package site.metacoding.bank.domain.account;

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

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.metacoding.bank.domain.AudingTime;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.enums.ResponseEnum;
import site.metacoding.bank.handler.exception.CustomApiException;

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

    private String password; // 계좌 비밀번호

    @Column(nullable = false)
    private Long balance; // 잔액

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

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
    public Account(Long id, Long number, String password, Long balance, User user) {
        this.id = id;
        this.number = number;
        this.password = password;
        this.balance = balance;
        this.user = user;
    }

    /*
     * 출금
     */
    public void withdraw(Long amount) {
        if (amount > balance) {
            throw new CustomApiException(ResponseEnum.LACK_BALANCE);
        }
        balance = balance - amount;
    }

    /*
     * 입금
     */
    public void deposit(Long amount) {
        balance = balance + amount;
    }

    /*
     * 계좌 소유자 확인
     */
    public void isAccountOwner(Long userId) {
        if (user.getId() != userId) {
            throw new CustomApiException(ResponseEnum.ISNOT_ACCOUNT_OWNER);
        }
    }

}
