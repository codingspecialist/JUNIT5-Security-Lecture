package site.metacoding.market.domain.user.buyer;

import javax.persistence.EntityManager;

import lombok.RequiredArgsConstructor;

interface BuyerDao {

}

@RequiredArgsConstructor
public class BuyerDaoImpl implements BuyerDao {
    private final EntityManager em;
}
