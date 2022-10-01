package site.metacoding.market.domain.user.seller;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import site.metacoding.market.domain.user.User;

@Getter
@Entity
public class Seller extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Seller의 PK이자 User의 FK
    private String name;
    private String tel;
}
