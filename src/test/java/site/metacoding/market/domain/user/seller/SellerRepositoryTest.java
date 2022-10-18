package site.metacoding.market.domain.user.seller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import site.metacoding.market.enums.RoleEnum;
import site.metacoding.market.web.dto.SellerBaseDto.SellerReqPost;

@DataJpaTest
public class SellerRepositoryTest {

    @Autowired
    private SellerRepository sellerRepository;

    @Test
    public void save_test() {
        // given
        SellerReqPost sellerPost = new SellerReqPost();
        sellerPost.setUsername("cos");

        sellerPost.setEmail("cos@nate.com");
        sellerPost.setRole(RoleEnum.SELLER.name());
        sellerPost.setSellerName("코스");
        sellerPost.setSellerTel("01022227777");

        Seller seller = Seller.create(sellerPost);

        // when
        Seller sellerPS = sellerRepository.save(seller);

        // then
        Assertions.assertThat(sellerPS.getSellerName()).isEqualTo(sellerPost.getSellerName());
        Assertions.assertThat(sellerPS.getUser().getUsername()).isEqualTo(sellerPost.getUsername());
    }
}
