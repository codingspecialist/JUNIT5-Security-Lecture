package site.metacoding.bank.dto.account;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.Setter;
import site.metacoding.bank.config.enums.TransactionEnum;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.user.User;

public class AccountRespDto {

    @Getter
    @Setter
    public static class AccountDetailRespDto {

        private Long id;
        private Long number;
        private String ownerName;
        private Long balance;
        private UserDto user;
        private List<TransactionDto> transactions = new ArrayList<>();

        private List<TransactionDto> reduceTransactionDto(List<Transaction> withdrawTransaction,
                List<Transaction> depositTransaction) {
            List<Transaction> transactions = Stream.concat(withdrawTransaction.stream(), depositTransaction.stream())
                    .sorted(Comparator.comparingLong((transaction) -> transaction.getId()))
                    .collect(Collectors.toList());

            return transactions.stream().map(TransactionDto::new).collect(Collectors.toList());
        }

        public AccountDetailRespDto(Account account) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.ownerName = account.getOwnerName();
            this.balance = account.getBalance();
            this.user = new UserDto(account.getUser());
            this.transactions = reduceTransactionDto(account.getWithdrawTransactions(),
                    account.getDepositTransactions());
        }

        @Getter
        @Setter
        public class UserDto {
            private Long id;
            private String username;

            public UserDto(User user) {
                this.id = user.getId();
                this.username = user.getUsername();
            }
        }

        @Getter
        @Setter
        public class TransactionDto {

            private Long id;
            private Long amount;
            private Long balance;
            private String gubun;
            private String createdAt;
            private String from;
            private String to;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.amount = transaction.getAmount();
                this.gubun = transaction.getGubun().getValue();
                if (transaction.getGubun() == TransactionEnum.WITHDRAW) {
                    this.createdAt = transaction.getCreatedAt()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    this.balance = transaction.getWithdrawAccountBalance();
                    this.from = transaction.getWithdrawAccount().getNumber() + "";
                    this.to = "ATM";
                }
                if (transaction.getGubun() == TransactionEnum.DEPOSIT) {
                    this.createdAt = transaction.getCreatedAt()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    this.balance = transaction.getDepositAccountBalance();
                    this.from = "ATM";
                    this.to = transaction.getDepositAccount().getNumber() + ""; // toString()??? ?????????, LazyLoading?????????!!
                }
                if (transaction.getGubun() == TransactionEnum.TRANSFER) {
                    this.createdAt = transaction.getCreatedAt()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    this.balance = transaction.getWithdrawAccountBalance();
                    this.from = transaction.getWithdrawAccount().getNumber() + "";
                    this.to = transaction.getDepositAccount().getNumber() + "";
                }
            }

        }

    }

    @Getter
    @Setter
    public static class AccountListRespDto {

        private List<AccountDto> accounts = new ArrayList<>();

        public AccountListRespDto(List<Account> accounts) {
            this.accounts = accounts.stream()
                    // .peek((account) -> {
                    // System.out.println(account.getNumber());
                    // })
                    .map(AccountDto::new)
                    .collect(Collectors.toList());
        }

        @Setter
        @Getter
        public class AccountDto {
            private Long id;
            private Long number;
            private String ownerName;
            private Long balance;

            public AccountDto(Account account) {
                this.id = account.getId();
                this.number = account.getNumber();
                this.ownerName = account.getOwnerName();
                this.balance = account.getBalance();
            }
        }
    }

    @Setter
    @Getter
    public static class AccountSaveRespDto {
        private Long id;
        private Long number;
        private String ownerName;
        private Long balance;

        public AccountSaveRespDto(Account account) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.ownerName = account.getOwnerName();
            this.balance = account.getBalance();
        }
    }

    @Setter
    @Getter
    public static class AccountDeleteRespDto {
        private Long accountNumber;
        private Boolean isUse;
        private String deleteDate;

        public AccountDeleteRespDto(Account account) {
            this.accountNumber = account.getNumber();
            this.isUse = account.getIsActive();
            // ???????????? ????????? ?????? ????????? DB??? ??????.
            this.deleteDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

    }
}
