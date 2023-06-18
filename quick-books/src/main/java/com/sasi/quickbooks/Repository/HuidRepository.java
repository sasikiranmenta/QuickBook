package com.sasi.quickbooks.Repository;

import com.sasi.quickbooks.model.huid.Huid;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HuidRepository extends MongoRepository<Huid, String> {

    @Query(value = "{createdOn:  {$gte:  ?0, $lte:  ?1}, saledOn: {$exists:  true} }", sort = "{createdOn:  1, _id:  1}")
    List<Huid> fetchHuidBasedOnStockIncludeOnlySale(Date from, Date to);

    @Query(value = "{createdOn:  {$gte:  ?0, $lte:  ?1}, saledOn: {$exists:  false} }", sort = "{createdOn:  1, _id:  1}")
    List<Huid> fetchHuidBasedOnStockIncludeOnlyStock(Date from, Date to);

    @Query(value = "{createdOn:  {$gte:  ?0, $lte:  ?1}}", sort = "{createdOn:  1, _id:  1}")
    List<Huid> fetchHuidBasedOnStock(Date from, Date to);

    @Query(value = "{$or: [{saledOn: {$exists: false}}, {saledOn: {$gte: ?0, $lte: ?1}}], saledOn:  {$exists:  false}}", sort = "{createdOn:  1, _id:  1}")
    List<Huid> fetchHuidBasedOnSaleIncludeOnlyStock(Date from, Date to);

    @Query(value = "{$or: [{saledOn: {$exists: false}}, {saledOn: {$gte: ?0, $lte: ?1}}], saledOn:  {$exists:  true}}", sort = "{createdOn:  1, _id:  1}")
    List<Huid> fetchHuidBasedOnSaleIncludeOnlySale(Date from, Date to);

    @Query(value = "{$or: [{saledOn: {$exists: false}}, {saledOn: {$gte: ?0, $lte: ?1}}]}", sort = "{createdOn:  1, _id:  1}")
    List<Huid> fetchHuidBasedOnSale(Date from, Date to);
}
