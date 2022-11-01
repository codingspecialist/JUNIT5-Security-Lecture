package site.metacoding.bank.dto;

import lombok.Getter;
import lombok.Setter;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.user.User;

public class AccountRespDto {

    @Getter
    @Setter
    public static class AccountDetailRespDto {
        private Long id;
        private Long number;
        private String password;
        private Long balance;
        private UserDto user;

        public AccountDetailRespDto(Account account) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.password = account.getPassword();
            this.balance = account.getBalance();
            this.user = new UserDto(account.getUser());
        }

        @Setter
        @Getter
        public class UserDto {
            private Long id;
            private String username;

            public UserDto(User user) {
                this.id = user.getId();
                this.username = user.getUsername();
            }
        }
    }

    @Getter
    @Setter
    public static class AccountAllRespDto {
        private Long id;
        private Long number;
        private String password;
        private Long balance;
        private UserDto user;

        public AccountAllRespDto(Account account) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.password = account.getPassword();
            this.balance = account.getBalance();
            this.user = new UserDto(account.getUser());
        }

        @Setter
        @Getter
        public class UserDto {
            private Long id;
            private String username;

            public UserDto(User user) {
                this.id = user.getId();
                this.username = user.getUsername();
            }
        }
    }

    @Setter
    @Getter
    public static class AccountSaveRespDto {
        private Long id;
        private Long number;
        private String password;
        private Long balance;
        private UserDto user;

        public AccountSaveRespDto(Account account) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.password = account.getPassword();
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
