package site.metacoding.market.domain.user.seller;

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
public class Seller {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String SellerName;
    @Column(nullable = false)
    private String SellerTel;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;
}
