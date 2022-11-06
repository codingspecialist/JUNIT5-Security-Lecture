package site.metacoding.bank.dto.account;

import lombok.Getter;
import lombok.Setter;
import site.metacoding.bank.domain.account.Account;

public class AccountRespDto {

    @Getter
    @Setter
    public static class AccountDetailRespDto {
        private Long id;
        private Long number;
        private Long balance;

        public AccountDetailRespDto(Account account) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
        }

    }

    @Getter
    @Setter
    public static class AccountAllRespDto {
        private Long id;
        private Long number;
        private Long balance;

        public AccountAllRespDto(Account account) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
        }
    }

    @Setter
    @Getter
    public static class AccountSaveRespDto {
        private Long id;
        private Long number;
        private String password;
        private Long balance;

        public AccountSaveRespDto(Account account) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.password = account.getPassword();
            this.balance = account.getBalance();
        }
    }
}
