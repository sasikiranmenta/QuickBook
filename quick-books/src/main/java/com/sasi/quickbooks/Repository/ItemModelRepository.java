package com.sasi.quickbooks.Repository;

import com.sasi.quickbooks.model.huid.ItemModels;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemModelRepository extends MongoRepository<ItemModels, String> {
    ItemModels findItemModelsByItemType(String itemModelType);
}
