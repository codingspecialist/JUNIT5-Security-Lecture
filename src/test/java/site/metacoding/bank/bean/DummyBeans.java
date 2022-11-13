package site.metacoding.bank.bean;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import site.metacoding.bank.config.enums.UserEnum;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.user.User;

public class DummyBeans {

    protected User newUser(String username) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        User user = User.builder()
                .username(username)
                .password(encPassword)
                .email(username + "@nate.com")
                .role(username.equals("admin") ? UserEnum.ADMIN : UserEnum.CUSTOMER)
                .build();
        return user;
    }

    protected Account newAccount(Long number, String ownername, User user) {
        Account account = Account.builder()
                .number(number)
                .password("1234")
                .ownerName(user.getUsername())
                .balance(1000L)
                .user(user)
                .isUse(true)
                .build();
        return account;
    }

    protected Transaction newWithdrawTransaction(Long amount, Account withdrawAccount) {
        Transaction transaction = withdrawAccount.withdraw(amount);
        return transaction;
    }

    protected Transaction newDepositTransaction(Long amount, Account depositAccount) {
        Transaction transaction = depositAccount.deposit(amount);
        return transaction;
    }

    protected Transaction newTransferTransaction(Long amount, Account withdrawAccount,
            Account depositAccount) {
        Transaction transaction = withdrawAccount.transper(amount, depositAccount);
        return transaction;
    }
}
