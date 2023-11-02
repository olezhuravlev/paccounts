package org.paccounts.repository;

import org.bson.types.ObjectId;
import org.paccounts.model.Currency;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Cacheable("currencies")
@Repository
public interface CurrencyRepo extends MongoRepository<Currency, ObjectId> {

    Optional<Currency> findByCode(String code);
    void deleteByCode(String code);
}
