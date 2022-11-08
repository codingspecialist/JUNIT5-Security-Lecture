package site.metacoding.bank.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import site.metacoding.bank.config.enums.TransactionEnum;
import site.metacoding.bank.config.enums.UserEnum;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.transaction.TransactionRepository;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.domain.user.UserRepository;

@ActiveProfiles("test")
@DataJpaTest
public class TransactionRepositoryTest {
        private final Logger log = LoggerFactory.getLogger(getClass());
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private AccountRepository accountRepository;
        @Autowired
        private TransactionRepository transactionRepository;
        @Autowired
        private EntityManager em;

        private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        @BeforeEach
        public void setUp() {
                autoincrementReset();
                dataSetting();
        }

        @Test
        public void findByDepositHistory() throws Exception {
                // given
                Long accountId = 1L;
                String gubun = "DEPOSIT";

                // when
                List<Transaction> transactions = transactionRepository.findByTransactionHistory(accountId, gubun);

                // then
                // 10만원에서 1만원 입금 -> 잔액 : 11만원
                assertThat(transactions.get(0).getAmount()).isEqualTo(10000L);
                assertThat(transactions.get(0).getDepositAccountBalance()).isEqualTo(110000L);
        }

        @Test
        public void findByWithdrawHistory() throws Exception {
                // given
                Long accountId = 1L;
                String gubun = "WITHDRAW";

                // when
                List<Transaction> transactions = transactionRepository.findByTransactionHistory(accountId, gubun);

                // then
                // 11만원에서 5천원 출금 -> 잔액 : 105000원
                assertThat(transactions.get(0).getAmount()).isEqualTo(5000L);
                assertThat(transactions.get(0).getWithdrawAccountBalance()).isEqualTo(105000L);
        }

        @Test
        public void findByTransactionHistory() throws Exception {
                // given
                Long accountId = 1L;
                String gubun = null;

                // when
                List<Transaction> transactions = transactionRepository.findByTransactionHistory(accountId, gubun);

                // then
                // 105000원에서 6만원 이체 -> 잔액 : 45000원
                assertThat(transactions.get(2).getAmount()).isEqualTo(60000L);
                assertThat(transactions.get(2).getWithdrawAccountBalance()).isEqualTo(45000L);
        }

        // @DataJpaTest에서는 truncate를 해도 auto_increment가 초기화 되지 않아서 아래 방법 사용
        private void autoincrementReset() {
                this.em
                                .createNativeQuery("ALTER TABLE transaction ALTER COLUMN `id` RESTART WITH 1")
                                .executeUpdate();
                this.em
                                .createNativeQuery("ALTER TABLE account ALTER COLUMN `id` RESTART WITH 1")
                                .executeUpdate();
                this.em
                                .createNativeQuery("ALTER TABLE users ALTER COLUMN `id` RESTART WITH 1")
                                .executeUpdate();
        }

        public void dataSetting() {
                String encPassword = passwordEncoder.encode("1234");
                User customer1 = User.builder().username("ssar").password(encPassword).email("ssar@nate.com")
                                .role(UserEnum.CUSTOMER)
                                .build();
                User customer1PS = userRepository.save(customer1);
                log.debug("디버그 : id:1, username: ssar 유저 생성");
                log.debug("디버그 : " + customer1PS.getId());

                User customer2 = User.builder().username("cos").password(encPassword).email("cos@nate.com")
                                .role(UserEnum.CUSTOMER)
                                .build();
                User customer2PS = userRepository.save(customer2);
                log.debug("디버그 : id:2, username: cos 유저 생성");
                log.debug("디버그 : " + customer2PS.getId());

                User admin = User.builder().username("admin").password(encPassword).email("admin@nate.com")
                                .role(UserEnum.ADMIN)
                                .build();
                User adminPS = userRepository.save(admin);
                log.debug("디버그 : id:3, username: admin 관리자 생성");
                log.debug("디버그 : " + adminPS.getId());

                Account account1 = Account.builder()
                                .number(1111L)
                                .password("1234")
                                .balance(100000L)
                                .user(customer1PS)
                                .build();
                Account account1PS = accountRepository.save(account1);
                log.debug("디버그 : ssar 고객 1111 계좌 생성 , 잔액 100000");

                Account account2 = Account.builder()
                                .number(2222L)
                                .password("1234")
                                .balance(100000L)
                                .user(customer1PS)
                                .build();
                Account account2PS = accountRepository.save(account2);
                log.debug("디버그 : ssar 고객 2222 계좌 생성 , 잔액 100000");

                Account account3 = Account.builder()
                                .number(3333L)
                                .password("1234")
                                .balance(100000L)
                                .user(customer2PS)
                                .build();
                Account account3PS = accountRepository.save(account3);
                log.debug("디버그 : cos 고객 3333 계좌 생성 , 잔액 100000");

                // ATM -> 계좌 (입금)
                Transaction transaction1 = Transaction.builder()
                                .withdrawAccount(null) // ATM -> 계좌
                                .depositAccount(account1PS)
                                .amount(10000L)
                                .depositAccountBalance(110000L)
                                .gubun(TransactionEnum.DEPOSIT)
                                .build();
                Transaction trasactionPS1 = transactionRepository.save(transaction1);
                account1PS.deposit(trasactionPS1);
                log.debug("디버그 : ATM -> 1111계좌(ssar), 입금액 : 10000, 잔액 : 110000 ");

                // 계좌 -> ATM (출금)
                Transaction transaction2 = Transaction.builder()
                                .withdrawAccount(account1PS) // 계좌 -> ATM
                                .depositAccount(null)
                                .amount(5000L)
                                .withdrawAccountBalance(105000L)
                                .gubun(TransactionEnum.WITHDRAW)
                                .build();
                Transaction trasactionPS2 = transactionRepository.save(transaction2);
                account1PS.withdraw(trasactionPS2);
                log.debug("디버그 : 1111계좌(ssar) -> ATM, 출금액 : 5000, 잔액 : 105000 ");

                // 계좌1 -> 계좌3
                Transaction transaction3 = Transaction.builder()
                                .withdrawAccount(account1PS) // 계좌 -> 계좌
                                .depositAccount(account3PS)
                                .amount(60000L)
                                .withdrawAccountBalance(45000L)
                                .depositAccountBalance(160000L)
                                .gubun(TransactionEnum.TRANSPER)
                                .build();
                Transaction trasactionPS3 = transactionRepository.save(transaction3);
                account1PS.withdraw(trasactionPS3);
                account3PS.deposit(trasactionPS3);
                log.debug("디버그 : 1111계좌(ssar) -> 3333계좌(cos), 이체액 : 60000, 1111계좌잔액 : 45000 , 3333계좌잔액: 160000");

                // 계좌2 -> 계좌1
                Transaction transaction4 = Transaction.builder()
                                .withdrawAccount(account2PS) // 계좌 -> 계좌
                                .depositAccount(account1PS)
                                .amount(30000L)
                                .withdrawAccountBalance(70000L)
                                .depositAccountBalance(75000L)
                                .gubun(TransactionEnum.TRANSPER)
                                .build();
                Transaction trasactionPS4 = transactionRepository.save(transaction4);
                account2PS.withdraw(trasactionPS4);
                account1PS.deposit(trasactionPS4);
                log.debug("디버그 : 2222계좌(ssar) -> 1111계좌(ssar), 이체액 : 30000, 1111계좌잔액 : 75000 , 2222계좌잔액: 70000");
        }
}
