package site.metacoding.bank.dto.account;

import lombok.Getter;
import lombok.Setter;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.user.User;

public class AccountReqDto {
    @Setter
    @Getter
    public static class AccountSaveReqDto {
        private Long number;
        private String password;
        private String ownerName;

        public Account toEntity(User user) {
            return Account.builder()
                    .number(number)
                    .password(password)
                    .ownerName(ownerName)
                    .balance(0L)
                    .user(user)
                    .isUse(true)
                    .build();
        }
    }

    @Setter
    @Getter
    public static class AccountDeleteReqDto {
        private String accountPassword;
    }
}
