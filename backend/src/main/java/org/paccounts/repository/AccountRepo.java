package org.paccounts.repository;

import org.bson.types.ObjectId;
import org.paccounts.model.Account;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Cacheable("accounts")
@Repository
public interface AccountRepo extends MongoRepository<Account, ObjectId> {

    Optional<Account> findByCode(String code);
    void deleteByCode(String code);
}
