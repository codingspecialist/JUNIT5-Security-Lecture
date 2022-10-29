package site.metacoding.market.dto;

import lombok.Getter;
import lombok.Setter;
import site.metacoding.market.domain.user.User;

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
