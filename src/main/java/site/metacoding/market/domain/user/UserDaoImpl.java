package site.metacoding.market.domain.user;

import javax.persistence.EntityManager;

import lombok.RequiredArgsConstructor;

interface UserDao {

}

// 복잡한 화면용 쿼리 모음, Lazy까지 수행함.
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {
    private final EntityManager em;

}
