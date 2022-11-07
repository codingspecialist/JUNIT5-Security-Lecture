package site.metacoding.bank.dto.account;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import site.metacoding.bank.domain.account.Account;
import site.metacoding.bank.domain.user.User;

@Slf4j
public class AccountReqDto {
    @Setter
    @Getter
    public static class AccountSaveReqDto {
        private Long number;
        private String password;

        public Account toEntity(User user) {
            return Account.builder()
                    .number(number)
                    .password(password)
                    .balance(0L)
                    .user(user)
                    .build();
        }
    }
}
