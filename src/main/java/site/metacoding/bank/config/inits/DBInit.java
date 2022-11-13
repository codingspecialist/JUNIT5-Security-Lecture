package site.metacoding.bank.config.inits;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.RequiredArgsConstructor;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.transaction.TransactionRepository;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.domain.user.UserRepository;

@RequiredArgsConstructor
@Configuration
public class DBInit extends DummyBeans {
        private final Logger log = LoggerFactory.getLogger(getClass());

        @Profile("dev")
        @Bean
        public CommandLineRunner demo(UserRepository userRepository, AccountRepository accountRepository,
                        TransactionRepository transactionRepository) {

                return (args) -> {
                        User ssarUser = userRepository.save(newUser("ssar"));
                        User cosUser = userRepository.save(newUser("cos"));
                        User adminUser = userRepository.save(newUser("admin"));
                        Account ssarAccount1 = accountRepository.save(newAccount(1111L, "쌀", ssarUser));
                        Account ssarAccount2 = accountRepository.save(newAccount(2222L, "쌀", ssarUser));
                        Account cosAccount1 = accountRepository.save(newAccount(3333L, "코스", cosUser));
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
                };
        }
}
