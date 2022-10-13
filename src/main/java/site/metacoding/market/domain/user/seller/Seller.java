package site.metacoding.market.domain.user.seller;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Getter;
import site.metacoding.market.domain.user.User;
import site.metacoding.market.web.dto.seller.SellerPost;

@Getter
@Entity
public class Seller {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String sellerName;
    @Column(nullable = false)
    private String sellerTel;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User user;

    public static Seller create(SellerPost sellerPost) {
        Seller seller = new Seller();
        seller.sellerName = sellerPost.getSellerName();
        seller.sellerTel = sellerPost.getSellerTel();
        seller.user = User.create(sellerPost.getUsername(), sellerPost.getPassword(), sellerPost.getEmail(),
                sellerPost.getRole());
        return seller;
    }
}
