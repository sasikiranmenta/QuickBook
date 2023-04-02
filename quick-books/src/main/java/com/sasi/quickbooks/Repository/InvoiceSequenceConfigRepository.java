package com.sasi.quickbooks.Repository;

import com.sasi.quickbooks.model.config.InvoiceSequenceConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceSequenceConfigRepository extends MongoRepository<InvoiceSequenceConfig, Integer> {

}
