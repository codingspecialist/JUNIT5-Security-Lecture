package site.metacoding.market.web.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import site.metacoding.market.domain.user.seller.Seller;

public class SellerBaseDto {

    @Setter
    @Getter
    public static class SellerReqPost {
        // seller
        private String sellerName;
        private String sellerTel;

        // user
        private String username;
        private String password;
        private String email;
        private String role; // enum 이지만 통신으로 받아야 하는데 데이터
    }

    @Setter
    @Getter
    public static class SellerResp {
        // seller
        private String sellerName;
        private String sellerTel;

        // user
        private String username;
        private String password;
        private String email;
        private String role; // enum 이지만 통신으로 받아야 하는데 데이터

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static SellerResp create(Seller seller) {
            SellerResp sellerResp = new SellerResp();
            sellerResp.sellerName = seller.getSellerName();
            sellerResp.sellerTel = seller.getSellerTel();
            sellerResp.username = seller.getUser().getUsername();
            sellerResp.password = seller.getUser().getPassword();
            sellerResp.email = seller.getUser().getEmail();
            sellerResp.role = seller.getUser().getRole().name();
            sellerResp.createdAt = seller.getUser().getCreatedAt();
            sellerResp.updatedAt = seller.getUser().getUpdatedAt();
            return sellerResp;
        }
    }

    @Setter
    @Getter
    public static class SellerReqUpdate {

    }

}
