package site.metacoding.market.domain.user.buyer;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
    @Column(nullable = false)
    private String buyerName;
    @Column(nullable = false)
    private String buyerTel;
    @Column(nullable = false)
    private String buyerAddress;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User user;
}
