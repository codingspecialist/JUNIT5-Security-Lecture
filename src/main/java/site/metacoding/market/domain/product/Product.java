package site.metacoding.market.domain.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Getter;
import site.metacoding.market.domain.AudingTime;
import site.metacoding.market.domain.user.seller.Seller;

@Getter
@Entity
public class Product extends AudingTime {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 50)
    private String productName;
    @Column(nullable = false)
    private Integer productPrice;
    @Column(nullable = false)
    private Integer productQty;

    @ManyToOne(fetch = FetchType.LAZY)
    private Seller seller;
}
