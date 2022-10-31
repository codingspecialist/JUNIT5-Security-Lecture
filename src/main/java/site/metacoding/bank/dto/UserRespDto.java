package site.metacoding.bank.dto;

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
}
