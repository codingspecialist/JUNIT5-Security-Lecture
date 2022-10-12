package site.metacoding.market.domain.purchase;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Getter;
import site.metacoding.market.domain.AudingTime;
import site.metacoding.market.domain.product.Product;
import site.metacoding.market.domain.user.buyer.Buyer;

@Getter
@Entity
public class Purchase extends AudingTime {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Buyer buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;
}
