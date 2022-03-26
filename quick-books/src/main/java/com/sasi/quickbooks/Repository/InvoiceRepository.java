package com.sasi.quickbooks.Repository;

import com.sasi.quickbooks.model.QuickBookInvoice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends CrudRepository<QuickBookInvoice, Long> {
}
