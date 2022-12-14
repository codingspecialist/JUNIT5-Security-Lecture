package site.metacoding.bank.dto.user;

import lombok.Getter;
import lombok.Setter;
import site.metacoding.bank.domain.user.User;

public class UserRespDto {
    @Setter
    @Getter
    public static class UserJoinRespDto {
        private Long id;
        private String username;

        public UserJoinRespDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
        }
    }

    @Setter
    @Getter
    public static class UserLoginRespDto {
        private Long id;
        private String username;

        public UserLoginRespDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
        }
    }
}
