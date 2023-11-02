package org.paccounts.repository;

import org.bson.types.ObjectId;
import org.paccounts.model.Operation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationRepo extends MongoRepository<Operation, ObjectId> {
}
