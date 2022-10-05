package site.metacoding.market.domain.product;

import javax.persistence.EntityManager;

import lombok.RequiredArgsConstructor;

interface Dao {
}

@RequiredArgsConstructor
public class ProductDao implements Dao {
    private final EntityManager em;
}
