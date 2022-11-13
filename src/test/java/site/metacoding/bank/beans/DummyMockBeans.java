package site.metacoding.bank.beans;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import site.metacoding.bank.config.enums.UserEnum;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.user.User;

public class DummyMockBeans {

    protected ObjectMapper om;

    public DummyMockBeans() {
        om = new ObjectMapper();
        // Object Mapper LocalDateTime 역직렬화 문제 해결
        om.registerModule(new JavaTimeModule());
    }

    protected User userCopy(User user) {
        try {
            User userCopy = om.readValue(om.writeValueAsString(user), User.class);
            return userCopy;
        } catch (Exception e) {
            throw new RuntimeException("서비스 테스트시 깊은 복사 실패 : " + e.getMessage());
        }
    }

    protected Account accountCopy(Account account) {
        try {
            Account accountCopy = om.readValue(om.writeValueAsString(account), Account.class);
            return accountCopy;
        } catch (Exception e) {
            throw new RuntimeException("서비스 테스트시 깊은 복사 실패 : " + e.getMessage());
        }
    }

    protected Transaction transactionCopy(Transaction transaction) {
        try {
            Transaction transactionCopy = om.readValue(om.writeValueAsString(transaction), Transaction.class);
            return transactionCopy;
        } catch (Exception e) {
            throw new RuntimeException("서비스 테스트시 깊은 복사 실패 : " + e.getMessage());
        }
    }

    protected User newUser(Long id, String username) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        User user = User.builder()
                .username(username)
                .password(encPassword)
                .email(username + "@nate.com")
                .role(username.equals("admin") ? UserEnum.ADMIN : UserEnum.CUSTOMER)
                .build();
        user.setMockData(id);
        return user;
    }

    protected Account newAccount(Long id, Long number, String ownername, User user) {
        Account account = Account.builder()
                .number(number)
                .password("1234")
                .ownerName(user.getUsername())
                .balance(1000L)
                .user(user)
                .isActive(true)
                .build();
        account.setMockData(id);
        return account;
    }

    protected Transaction newWithdrawTransaction(Long id, Long amount, Account withdrawAccount) {
        Transaction transaction = withdrawAccount.withdraw(amount);
        transaction.setMockData(id);
        return transaction;
    }

    protected Transaction newDepositTransaction(Long id, Long amount, Account depositAccount) {
        Transaction transaction = depositAccount.deposit(amount);
        transaction.setMockData(id);
        return transaction;
    }

    protected Transaction newTransferTransaction(Long id, Long amount, Account withdrawAccount,
            Account depositAccount) {
        Transaction transaction = withdrawAccount.transper(amount, depositAccount);
        transaction.setMockData(id);
        return transaction;
    }
}
