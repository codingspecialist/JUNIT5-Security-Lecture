package site.metacoding.market.domain.product;

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

    private String productName;
    private Integer productPrice;
    private Integer productQty;

    @ManyToOne(fetch = FetchType.LAZY)
    private Seller seller;
}
