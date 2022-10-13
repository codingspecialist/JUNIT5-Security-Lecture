package site.metacoding.market.domain.user.seller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import site.metacoding.market.domain.user.seller.Seller;
import site.metacoding.market.domain.user.seller.SellerRepository;
import site.metacoding.market.web.dto.seller.SellerPost;

@DataJpaTest
public class SellerRepositoryTest {

    @Autowired
    private SellerRepository sellerRepository;

    @Test
    public void save_test() {
        // given
        SellerPost sellerPost = new SellerPost();
        sellerPost.setUsername("cos");

        sellerPost.setEmail("cos@nate.com");
        sellerPost.setRole("SELLER");
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
