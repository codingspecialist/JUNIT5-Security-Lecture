package site.metacoding.bank.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.metacoding.bank.domain.AudingTime;
import site.metacoding.bank.enums.UserEnum;

@NoArgsConstructor
@Getter
@Table(name = "users")
@Entity
public class User extends AudingTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserEnum role; // ADMIN, CUSTOMER

    @Builder
    public User(Long id, String username, String password, String email, UserEnum role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

}

// for (RoleEnum roleValue : RoleEnum.values()) {
// if (roleValue.name().equals(role)) {
// user.role = roleValue;
// }
// }