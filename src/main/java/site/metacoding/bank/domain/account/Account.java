package site.metacoding.bank.domain.account;

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
    private List<Transaction> withdrawAccounts;

    @OneToMany(mappedBy = "depositAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Transaction> depositAccounts;

    @Builder
    public Account(Long id, Long number, String password, Long balance, User user) {
        this.id = id;
        this.number = number;
        this.password = password;
        this.balance = balance;
        this.user = user;
    }

}
