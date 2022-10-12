package site.metacoding.market.domain.purchase;

import javax.persistence.EntityManager;

import lombok.RequiredArgsConstructor;

interface PurchaseDao {
}

@RequiredArgsConstructor
public class PurchaseDaoImpl implements PurchaseDao {
    private final EntityManager em;
}
