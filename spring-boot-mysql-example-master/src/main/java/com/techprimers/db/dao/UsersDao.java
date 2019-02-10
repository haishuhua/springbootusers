package com.techprimers.db.dao;

import com.techprimers.db.model.Users;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@CacheConfig(cacheNames = "users")
public class UsersDao extends BaseDaoImpl<Users, Integer> {

    @SuppressWarnings("unchecked")
    @Transactional
    @Cacheable
    public Users getUserFromId(Integer userId) {
        if (userId == null)
            return null;

        List<Users> list = (List<Users>) getCurrentSession()
                .createCriteria(Users.class)
                .add( Restrictions.eq("id", userId))
                .list();

        if (list == null || list.size() != 1)
            return null;

        return list.get(0);

    }

}

