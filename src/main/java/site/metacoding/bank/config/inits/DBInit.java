package site.metacoding.bank.config.inits;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.RequiredArgsConstructor;
import site.metacoding.bank.config.enums.TransactionEnum;
import site.metacoding.bank.config.enums.UserEnum;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.account.AccountRepository;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.transaction.TransactionRepository;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.domain.user.UserRepository;

@RequiredArgsConstructor
@Configuration
public class DBInit {
        private final Logger log = LoggerFactory.getLogger(getClass());
        private final BCryptPasswordEncoder passwordEncoder;

        @Profile("dev")
        @Bean
        public CommandLineRunner demo(UserRepository userRepository, AccountRepository accountRepository,
                        TransactionRepository transactionRepository) {

                return (args) -> {
                        String encPassword = passwordEncoder.encode("1234");
                        User customer1 = User.builder().username("ssar").password(encPassword).email("ssar@nate.com")
                                        .role(UserEnum.CUSTOMER)
                                        .build();
                        User customer1PS = userRepository.save(customer1);
                        log.debug("디버그 : id:1, username: ssar 유저 생성");

                        User customer2 = User.builder().username("cos").password(encPassword).email("cos@nate.com")
                                        .role(UserEnum.CUSTOMER)
                                        .build();
                        User customer2PS = userRepository.save(customer2);
                        log.debug("디버그 : id:2, username: cos 유저 생성");

                        User admin = User.builder().username("admin").password(encPassword).email("admin@nate.com")
                                        .role(UserEnum.ADMIN)
                                        .build();
                        userRepository.save(admin);
                        log.debug("디버그 : id:3, username: admin 관리자 생성");

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
                                        .gubun(TransactionEnum.TRANSFER)
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
                                        .gubun(TransactionEnum.TRANSFER)
                                        .build();
                        Transaction trasactionPS4 = transactionRepository.save(transaction4);
                        account2PS.withdraw(trasactionPS4);
                        account1PS.deposit(trasactionPS4);
                        log.debug("디버그 : 2222계좌(ssar) -> 1111계좌(ssar), 이체액 : 30000, 1111계좌잔액 : 75000 , 2222계좌잔액: 70000");
                };
        }
}
