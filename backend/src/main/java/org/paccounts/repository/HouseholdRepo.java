package org.paccounts.repository;

import org.bson.types.ObjectId;
import org.paccounts.model.Household;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Cacheable("households")
@Repository
public interface HouseholdRepo extends MongoRepository<Household, ObjectId> {

    @Query(value = "{'_id': :#{#householdId}}", fields = "{'articles':{'$elemMatch':{'_id': :#{#articleId}}}}")
    Optional<Household> findWithEmbeddedArticle(@Param("householdId") ObjectId householdId, @Param("articleId") ObjectId articleId);

    @Query(value = "{'_id': :#{#householdId}}", fields = "{'items':1}")
    Optional<Household> findWithEmbeddedItems(@Param("householdId") ObjectId householdId);
}
