package site.metacoding.market.domain.user.buyer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyerRepository extends JpaRepository<Buyer, Long>, BuyerDao {

}
