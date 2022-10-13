package site.metacoding.market.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.metacoding.market.domain.AudingTime;
import site.metacoding.market.enums.RoleEnum;

@Setter
@NoArgsConstructor
@Getter
@Table(name = "users")
@Entity

public class User extends AudingTime {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    public static User create(String username, String password, String email, String role) {
        User user = new User();
        user.username = username;
        user.password = password;
        user.email = email;

        for (RoleEnum roleValue : RoleEnum.values()) {
            if (roleValue.name().equals(role)) {
                user.role = roleValue;
            }
        }
        return user;
    }
}
