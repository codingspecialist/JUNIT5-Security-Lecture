package site.metacoding.bank.dto;

import lombok.Getter;
import lombok.Setter;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.transaction.Transaction;
import site.metacoding.bank.domain.user.User;

public class TransactionRespDto {
    @Getter
    @Setter
    public static class TransactionWithdrawRespDto {
        private Long id;
        private Long amount;
        private String gubun;
        private WithdrawAccount withdrawAccount;

        public TransactionWithdrawRespDto(Transaction transaction) {
            this.id = transaction.getId();
            this.amount = transaction.getAmount();
            this.gubun = transaction.getGubun().name();
            this.withdrawAccount = new WithdrawAccount(transaction.getWithdrawAccount());
        }

        @Getter
        @Setter
        public class WithdrawAccount {
            private Long id;
            private Long number;
            private Long balance;
            private UserDto user;

            public WithdrawAccount(Account account) {
                this.id = account.getId();
                this.number = account.getNumber();
                this.balance = account.getBalance();
                this.user = new UserDto(account.getUser());
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
        }
    }
}
