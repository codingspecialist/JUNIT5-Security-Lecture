package site.metacoding.bank.dto;

import lombok.Getter;
import lombok.Setter;
import site.metacoding.bank.domain.user.User;
import site.metacoding.bank.enums.UserEnum;

public class UserReqDto {
    @Setter
    @Getter
    public static class UserJoinReqDto {
        private String username;
        private String password;
        private String email;

        public User toEntity() {
            return User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(UserEnum.CUSTOMER)
                    .build();
        }
    }
}
