package site.metacoding.market.domain.purchase;

import javax.persistence.EntityManager;

import lombok.RequiredArgsConstructor;

interface Dao {
}

@RequiredArgsConstructor
public class PurchaseDao implements Dao {
    private final EntityManager em;
}
