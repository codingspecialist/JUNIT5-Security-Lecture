package site.metacoding.bank.dto.account;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import site.metacoding.bank.config.enums.TransactionEnum;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.transaction.Transaction;

public class AccountRespDto {

    @Getter
    @Setter
    public static class AccountDetailRespDto {
        private Long id;
        private Long number;
        private String ownerName;
        private Long balance;
        private List<TransactionDto> transactions = new ArrayList<>();

        public AccountDetailRespDto(Account account, List<Transaction> transactions) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.ownerName = account.getOwnerName();
            this.balance = account.getBalance();
            this.transactions = transactions.stream().map(TransactionDto::new).collect(Collectors.toList());
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

    @Getter
    @Setter
    public static class AccountAllRespDto {
        private Long id;
        private Long number;
        private String ownerName;
        private Long balance;

        public AccountAllRespDto(Account account) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.ownerName = account.getOwnerName();
            this.balance = account.getBalance();
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
            // 더티체킹 타이밍 보다 빨라서 DB값 못씀.
            this.deleteDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

    }
}
