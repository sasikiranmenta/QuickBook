package com.sasi.quickbooks.Repository;

import com.sasi.quickbooks.model.invoice.Invoice;
import com.sasi.quickbooks.model.QuickBookHSNEnum;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface InvoiceRepository extends MongoRepository<Invoice, ObjectId> {


    @Query("{$and: [" +
            "{billDate: {$gte: ?0}}," +
            "{billDate: {$lt: ?1}}, " +
            "{$or: [" +
                "{identificationNumberType : { $exists: false }}, {identificationNumberType: {$ne: \"GSTIN\"}}]}]}")
    List<Invoice> invoiceByBillDateBetweenNoGST(Date from, Date to);

    @Query("{$and: [" +
            "{billDate: {$gte: ?0}}," +
            "{billDate: {$lt: ?1}}, " +
            "{$or: [" +
            "{identificationNumberType : { $exists: false }}, {identificationNumberType: {$ne: \"GSTIN\"}}]}, {invoiceType: ?2}]}")
    List<Invoice> invoiceByBillDateBetweenNoGSTBasedOnType(Date from, Date to, QuickBookHSNEnum type);

    @Query("{$and: [" +
            "{billDate: {$gte: ?0}}," +
            "{billDate: {$lt: ?1}}, " +
            "{$and: [" +
            "{identificationNumberType : { $exists: true }}, {identificationNumberType: \"GSTIN\"}]}, {invoiceType: ?2}]}")
    List<Invoice> invoiceByBillDateBetweenOnlyGSTBasedOnType(Date from, Date to, QuickBookHSNEnum type);

    @Query("{$and: [" +
            "{billDate: {$gte: ?0}}," +
            "{billDate: {$lt: ?1}}, " +
            "{$and: [" +
            "{identificationNumberType : { $exists: true }}, {identificationNumberType: \"GSTIN\"}]}]}")
    List<Invoice> invoiceByBillDateBetweenOnlyGST(Date from, Date to);

    @Query("{$and: [{billDate: {$gte: ?0}}, {billDate: {$lt: ?1}}]}")
    List<Invoice> invoiceByBillDateBetween(Date from, Date to);

    Invoice getMongoInvoiceByInvoiceIdAndFinancialYear(int invoiceId, int financialYear);


}
