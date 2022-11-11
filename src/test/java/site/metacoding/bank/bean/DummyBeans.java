package site.metacoding.bank.bean;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import site.metacoding.bank.config.enums.TransactionEnum;
import site.metacoding.bank.config.enums.UserEnum;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.user.User;

public class DummyBeans {
    protected User newUser(Long id, String username) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        User user = User.builder()
                .id(id)
                .username(username)
                .password(encPassword)
                .email(username + "@nate.com")
                .role(username.equals("admin") ? UserEnum.ADMIN : UserEnum.CUSTOMER)
                .build();
        user.setTestDate();
        return user;
    }

    protected Account newAccount(Long id, Long number, User user) {
        Account account = Account.builder()
                .id(id)
                .number(number)
                .password("1234")
                .ownerName(user.getUsername())
                .balance(1000L)
                .user(user)
                .isUse(true)
                .build();
        account.setTestDate();
        return account;
    }

    protected Transaction newWithdrawTransaction(Long id, Account withdrawAccount) {
        // 출금 금액
        Long amount = 100L;
        // 잔액
        Long balance = withdrawAccount.getBalance() - amount;
        Transaction transaction = Transaction.builder()
                .id(id)
                .withdrawAccount(withdrawAccount)
                .depositAccount(null)
                .amount(amount)
                .withdrawAccountBalance(balance)
                .depositAccountBalance(null)
                .gubun(TransactionEnum.WITHDRAW)
                .build();
        transaction.setTestDate();
        withdrawAccount.withdraw(amount);

        return transaction;
    }

    protected Transaction newDepositTransaction(Long id, Account depositAccount) {
        // 입금 금액
        Long amount = 100L;
        // 잔액
        Long balance = depositAccount.getBalance() + amount;
        Transaction transaction = Transaction.builder()
                .id(id)
                .withdrawAccount(null)
                .depositAccount(depositAccount)
                .amount(amount)
                .withdrawAccountBalance(null)
                .depositAccountBalance(balance)
                .gubun(TransactionEnum.DEPOSIT)
                .build();
        transaction.setTestDate();
        depositAccount.deposit(amount);
        return transaction;
    }

    protected Transaction newTransferTransaction(Long id, Account withdrawAccount, Account depositAccount) {
        // 이체 금액
        Long amount = 100L;
        // 잔액
        Long withdrawBalance = withdrawAccount.getBalance() - amount;
        Long depositBalance = depositAccount.getBalance() + amount;

        Transaction transaction = Transaction.builder()
                .id(id)
                .withdrawAccount(withdrawAccount)
                .depositAccount(depositAccount)
                .amount(amount)
                .withdrawAccountBalance(withdrawBalance)
                .depositAccountBalance(depositBalance)
                .gubun(TransactionEnum.TRANSFER)
                .build();
        transaction.setTestDate();
        withdrawAccount.withdraw(amount);
        depositAccount.deposit(amount);

        return transaction;
    }
}
