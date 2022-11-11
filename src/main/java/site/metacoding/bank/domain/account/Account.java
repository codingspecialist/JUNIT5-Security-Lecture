package site.metacoding.bank.domain.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.metacoding.bank.config.enums.ResponseEnum;
import site.metacoding.bank.config.exceptions.CustomApiException;
import site.metacoding.bank.domain.AudingTime;
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

    private String password; // 계좌 비밀번호

    @Column(nullable = false)
    private Long balance; // 잔액

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // @OneToMany(mappedBy = "withdrawAccount", fetch = FetchType.LAZY, cascade =
    // CascadeType.ALL)
    // private List<Transaction> withdrawTransactions = new ArrayList<>();

    // @OneToMany(mappedBy = "depositAccount", fetch = FetchType.LAZY, cascade =
    // CascadeType.ALL)
    // private List<Transaction> depositTransactions = new ArrayList<>();

    // public void addWithdrawTransaction(Transaction transaction) {
    // this.withdrawTransactions.add(transaction);
    // }

    // public void addDepositTransaction(Transaction transaction) {
    // this.depositTransactions.add(transaction);
    // }

    @Builder
    public Account(Long id, Long number, String ownerName, String password, Long balance, User user) {
        this.id = id;
        this.number = number;
        this.ownerName = ownerName;
        this.password = password;
        this.balance = balance;
        this.user = user;
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
     * 입금
     */
    public void deposit(Long amount) {
        balance = balance + amount;
        // addDepositTransaction(transaction); // Account 순수객체시점에 Account로 Transactions
        // 조회하려면 필요함.
    }

    /*
     * 출금
     */
    public void withdraw(Long amount) {
        checkBalance(amount);
        balance = balance - amount;
        // addWithdrawTransaction(transaction); // Account 순수객체시점에 Account로 Transactions
        // 조회하려면 필요함.
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
