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
import org.springframework.test.context.ActiveProfiles;

import site.metacoding.bank.beans.DummyBeans;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.transaction.TransactionRepository;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.domain.user.UserRepository;

@ActiveProfiles("test")
@DataJpaTest
public class TransactionRepositoryTest extends DummyBeans {
        private final Logger log = LoggerFactory.getLogger(getClass());
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private AccountRepository accountRepository;
        @Autowired
        private TransactionRepository transactionRepository;
        @Autowired
        private EntityManager em;

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
                Integer page = 0;

                // when
                List<Transaction> transactions = transactionRepository.findByTransactionHistory(accountId, gubun, page);

                // then
                assertThat(transactions.get(0).getAmount()).isEqualTo(100L);
                assertThat(transactions.get(0).getDepositAccountBalance()).isEqualTo(900L);
        }

        @Test
        public void findByWithdrawHistory() throws Exception {
                // given
                Long accountId = 1L;
                String gubun = "WITHDRAW";
                Integer page = 0;

                // when
                List<Transaction> transactions = transactionRepository.findByTransactionHistory(accountId, gubun, page);

                // then
                assertThat(transactions.get(0).getAmount()).isEqualTo(100L);
                assertThat(transactions.get(0).getWithdrawAccountBalance()).isEqualTo(900L);
        }

        @Test
        public void findByTransactionHistory() throws Exception {
                // given
                Long accountId = 1L;
                String gubun = null;
                Integer page = 0;

                // when
                List<Transaction> transactions = transactionRepository.findByTransactionHistory(accountId, gubun, page);

                // then
                assertThat(transactions.get(0).getAmount()).isEqualTo(100L);
                assertThat(transactions.get(0).getWithdrawAccountBalance()).isEqualTo(900L);
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
                User ssarUser = userRepository.save(newUser("ssar"));
                User cosUser = userRepository.save(newUser("cos"));
                User adminUser = userRepository.save(newUser("admin"));
                Account ssarAccount1 = accountRepository.save(newAccount(1111L, "쌀", ssarUser));
                Account ssarAccount2 = accountRepository.save(newAccount(2222L, "쌀", ssarUser));
                Account ssarAccount3 = accountRepository.save(newAccount(3333L, "쌀", false, ssarUser));
                Account cosAccount1 = accountRepository.save(newAccount(4444L, "코스", cosUser));
                Transaction withdrawTransaction1 = transactionRepository
                                .save(newWithdrawTransaction(100L, ssarAccount1));
                Transaction withdrawTransaction2 = transactionRepository
                                .save(newWithdrawTransaction(100L, ssarAccount1));
                Transaction depositTransaction1 = transactionRepository
                                .save(newDepositTransaction(100L, ssarAccount1));
                Transaction transferTransaction1 = transactionRepository
                                .save(newTransferTransaction(100L, ssarAccount1, cosAccount1));
                Transaction transferTransaction2 = transactionRepository
                                .save(newTransferTransaction(100L, ssarAccount1, ssarAccount2));

        }
}
