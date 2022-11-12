package site.metacoding.bank.bean;

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

    protected Account newAccount(Long id, Long number, String ownername, User user) {
        // stub 시에 객체 상태가 변경되지 않게 해야한다.
        try {
            User userCopy = om.readValue(om.writeValueAsString(user), User.class);
            Account account = Account.builder()
                    .id(id)
                    .number(number)
                    .password("1234")
                    .ownerName(user.getUsername())
                    .balance(1000L)
                    .user(userCopy)
                    .isUse(true)
                    .build();
            account.setTestDate();
            return account;
        } catch (Exception e) {
            throw new RuntimeException("서비스 테스트시 깊은 복사 실패 : " + e.getMessage());
        }
    }

    protected Transaction newWithdrawTransaction(Long id, Long amount, Account withdrawAccount) {
        // stub 시에 객체 상태가 변경되지 않게 해야한다.
        try {
            Account withAccountCopy = om.readValue(om.writeValueAsString(withdrawAccount), Account.class);
            Transaction transaction = withAccountCopy.withdraw(amount);
            transaction.setTestDate();
            return transaction;
        } catch (Exception e) {
            throw new RuntimeException("서비스 테스트시 깊은 복사 실패");
        }

    }

    protected Transaction newDepositTransaction(Long id, Long amount, Account depositAccount) {
        // stub 시에 객체 상태가 변경되지 않게 해야한다.
        try {
            Account depositAccountCopy = om.readValue(om.writeValueAsString(depositAccount), Account.class);
            Transaction transaction = depositAccountCopy.deposit(amount);
            transaction.setTestDate();
            return transaction;
        } catch (Exception e) {
            throw new RuntimeException("서비스 테스트시 깊은 복사 실패");
        }

    }

    protected Transaction newTransferTransaction(Long id, Long amount, Account withdrawAccount,
            Account depositAccount) {
        // stub 시에 객체 상태가 변경되지 않게 해야한다.
        try {
            Account withAccountCopy = om.readValue(om.writeValueAsString(withdrawAccount), Account.class);
            Account depositAccountCopy = om.readValue(om.writeValueAsString(depositAccount), Account.class);
            Transaction transaction = withAccountCopy.transper(amount, depositAccountCopy);
            transaction.setTestDate();
            return transaction;
        } catch (Exception e) {
            throw new RuntimeException("서비스 테스트시 깊은 복사 실패");
        }

    }
}
