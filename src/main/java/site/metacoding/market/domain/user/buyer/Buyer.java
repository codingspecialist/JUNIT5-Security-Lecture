package site.metacoding.market.domain.user.buyer;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Getter;
import site.metacoding.market.domain.user.User;

@Getter
@Entity
public class Buyer {
    @Id
    @GeneratedValue
    private Long id;
    private String buyerName;
    private String buyerTel;
    private String buyerAddress;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;
}
