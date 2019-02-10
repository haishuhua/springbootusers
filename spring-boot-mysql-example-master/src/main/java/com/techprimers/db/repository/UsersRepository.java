package com.techprimers.db.repository;

import com.techprimers.db.model.Users;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

@CacheConfig(cacheNames = "users")
public interface UsersRepository extends JpaRepository<Users, Integer> {
    @Cacheable
    Users findByName(String name);

}