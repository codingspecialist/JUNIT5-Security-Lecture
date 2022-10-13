package site.metacoding.market.web.dto.seller;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SellerPost {

    // seller
    private String sellerName;
    private String sellerTel;

    // user
    private String username;
    private String password;
    private String email;
    private String role; // enum 이지만 통신으로 받아야 하는데 데이터
}
