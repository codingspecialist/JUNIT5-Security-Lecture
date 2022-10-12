package site.metacoding.market.domain.product;

import javax.persistence.EntityManager;

import lombok.RequiredArgsConstructor;

interface ProductDao {
}

@RequiredArgsConstructor
public class ProductDaoImpl implements ProductDao {
    private final EntityManager em;
}
