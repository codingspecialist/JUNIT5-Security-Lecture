package site.metacoding.market.domain.user.buyer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import site.metacoding.market.domain.user.User;

@Getter
@Entity
public class Buyer extends User {
    @Id
    @GeneratedValue
    private Long id; // Buyer의 PK이자 User의 FK
    private String name;
    private String tel;
    private String address;
}
