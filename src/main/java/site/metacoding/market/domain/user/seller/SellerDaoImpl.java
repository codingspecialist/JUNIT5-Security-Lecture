package site.metacoding.market.domain.user.seller;

import javax.persistence.EntityManager;

import lombok.RequiredArgsConstructor;

interface SellerDao {

}

@RequiredArgsConstructor
public class SellerDaoImpl implements SellerDao {
    private final EntityManager em;
}
