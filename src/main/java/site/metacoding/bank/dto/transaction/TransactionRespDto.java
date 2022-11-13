package site.metacoding.bank.dto.transaction;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import site.metacoding.bank.config.enums.TransactionEnum;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.user.User;

public class TransactionRespDto {

    @Setter
    @Getter
    public static class TransferRespDto {
        private Long id; // 계좌 ID
        private Long number; // 계좌번호
        private String ownerName;
        private Long balance;
        private TransactionDto transaction;

        public TransferRespDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.ownerName = account.getOwnerName();
            this.balance = account.getBalance();
            this.transaction = new TransactionDto(transaction);
        }

        @Getter
        @Setter
        public class TransactionDto {
            private Long id;
            private Long amount;
            private String gubun; // 이체
            private String from; // 출금계좌 (내계좌)
            private String to; // 입금계좌

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.amount = transaction.getAmount();
                this.gubun = transaction.getGubun().getValue();
                this.from = transaction.getWithdrawAccount().getNumber() + "";
                this.to = transaction.getDepositAccount().getNumber() + "";
            }

        }
    }

    @Setter
    @Getter
    public static class DepositRespDto {
        private Long id; // 계좌 ID
        private Long number; // 계좌번호
        private String ownerName;
        private Long balance;
        private TransactionDto transaction;

        public DepositRespDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.ownerName = account.getOwnerName();
            this.balance = account.getBalance();
            this.transaction = new TransactionDto(transaction);
        }

        @Getter
        @Setter
        public class TransactionDto {
            private Long id;
            private Long amount;
            private String gubun; // 입금
            private String from; // ATM

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.amount = transaction.getAmount();
                this.gubun = transaction.getGubun().getValue();
                this.from = "ATM";
            }

        }
    }

    @Setter
    @Getter
    public static class WithdrawRespDto {
        private Long id; // 계좌 ID
        private Long number; // 계좌번호
        private String ownerName;
        private Long balance;
        private TransactionDto transaction;

        public WithdrawRespDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.ownerName = account.getOwnerName();
            this.balance = account.getBalance();
            this.transaction = new TransactionDto(transaction);
        }

        @Getter
        @Setter
        public class TransactionDto {
            private Long id;
            private Long amount;
            private String gubun; // 출금
            private String to; // ATM

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.amount = transaction.getAmount();
                this.gubun = transaction.getGubun().getValue();
                this.to = "ATM";
            }

        }
    }

    @Getter
    @Setter
    public static class TransactionHistoryRespDto {
        private Long id;
        private Long number;
        private String ownerName;
        private Long balance;
        private UserDto user;

        private List<TransactionDto> transactions = new ArrayList<>();

        public TransactionHistoryRespDto(Account account, List<Transaction> transactions) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.ownerName = account.getOwnerName();
            this.balance = account.getBalance();
            this.user = new UserDto(account.getUser());
            this.transactions = transactions.stream().map(TransactionDto::new).collect(Collectors.toList());
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
                    this.to = transaction.getDepositAccount().getNumber() + ""; // toString()으 쓰지마, LazyLoading때문에!!
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
}
// @Getter
// @Setter
// public static class DepositHistoryRespDto {
// private Long id;
// private Long number;
// private Long balance;
// private UserDto user;

// private List<DepositTransactionDto> depositTransactions = new ArrayList<>();

// public DepositHistoryRespDto(Account account) {
// this.id = account.getId();
// this.number = account.getNumber();
// this.balance = account.getBalance();
// this.user = new UserDto(account.getUser());
// this.depositTransactions = account.getDepositTransactions()
// .stream().map(DepositTransactionDto::new).collect(Collectors.toList());
// }

// @Getter
// @Setter
// public class UserDto {
// private Long id;
// private String username;

// public UserDto(User user) {
// this.id = user.getId();
// this.username = user.getUsername();
// }
// }

// @Getter
// @Setter
// public class DepositTransactionDto {
// private Long id;
// private Long amount;
// private Long balance;
// private String gubun;
// private String createdAt;
// private String from;
// private String to;

// public DepositTransactionDto(Transaction transaction) {
// this.id = transaction.getId(); // Lazy Loading
// this.amount = transaction.getAmount();
// this.balance = transaction.getDepositAccountBalance();
// this.createdAt = transaction.getDepositAccount().getCreatedAt()
// .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
// this.gubun = transaction.getGubun().name();
// if (transaction.getGubun() == TransactionEnum.WITHDRAW) {
// this.from = transaction.getWithdrawAccount().getNumber().toString();
// this.to = "ATM";
// }
// if (transaction.getGubun() == TransactionEnum.DEPOSIT) {
// this.from = "ATM";
// this.to = transaction.getDepositAccount().getNumber().toString();
// }
// if (transaction.getGubun() == TransactionEnum.TRANSPER) {
// this.from = transaction.getWithdrawAccount().getNumber().toString();
// this.to = transaction.getDepositAccount().getNumber().toString();
// }
// }

// }
// }

// @Getter
// @Setter
// public static class WithdrawHistoryRespDto {
// private Long id;
// private Long number;
// private Long balance;
// private UserDto user;

// private List<WithdrawTransactionDto> withdrawTransactions = new
// ArrayList<>();

// public WithdrawHistoryRespDto(Account account) {
// this.id = account.getId();
// this.number = account.getNumber();
// this.balance = account.getBalance();
// this.user = new UserDto(account.getUser());
// this.withdrawTransactions = account.getWithdrawTransactions()
// .stream().map(WithdrawTransactionDto::new).collect(Collectors.toList());
// }

// @Getter
// @Setter
// public class UserDto {
// private Long id;
// private String username;

// public UserDto(User user) {
// this.id = user.getId();
// this.username = user.getUsername();
// }
// }

// @Getter
// @Setter
// public class WithdrawTransactionDto {
// private Long id;
// private Long amount;
// private Long balance;
// private String gubun;
// private String createdAt;
// private String from;
// private String to;

// public WithdrawTransactionDto(Transaction transaction) {
// this.id = transaction.getId(); // Lazy Loading
// this.amount = transaction.getAmount();
// this.balance = transaction.getWithdrawAccountBalance();
// this.createdAt = transaction.getWithdrawAccount().getCreatedAt()
// .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
// this.gubun = transaction.getGubun().name();
// if (transaction.getGubun() == TransactionEnum.WITHDRAW) {
// this.from = transaction.getWithdrawAccount().getNumber().toString();
// this.to = "ATM";
// }
// if (transaction.getGubun() == TransactionEnum.DEPOSIT) {
// this.from = "ATM";
// this.to = transaction.getDepositAccount().getNumber().toString();
// }
// if (transaction.getGubun() == TransactionEnum.TRANSPER) {
// this.from = transaction.getWithdrawAccount().getNumber().toString();
// this.to = transaction.getDepositAccount().getNumber().toString();
// }
// }

// }
// }
