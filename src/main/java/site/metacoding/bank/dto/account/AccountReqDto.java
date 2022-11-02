package site.metacoding.bank.dto.account;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import site.metacoding.bank.config.auth.LoginUser;
import site.metacoding.bank.domain.account.Account;

@Slf4j
public class AccountReqDto {
    @Setter
    @Getter
    public static class AccountSaveReqDto {
        private Long number;
        private String password;
        private LoginUser loginUser; // 서비스 로직

        public Account toEntity() {
            return Account.builder()
                    .number(number)
                    .password(password)
                    .balance(0L)
                    .user(loginUser.getUser())
                    .build();
        }
    }
}
