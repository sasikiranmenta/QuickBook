package com.sasi.quickbooks.Repository;

import com.sasi.quickbooks.model.huid.Huid;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HuidRepository extends MongoRepository<Huid, String> {
}
