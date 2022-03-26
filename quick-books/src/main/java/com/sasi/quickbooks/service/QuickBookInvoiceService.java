package com.sasi.quickbooks.service;

import com.itextpdf.text.DocumentException;
import com.sasi.quickbooks.Repository.InvoiceRepository;
import com.sasi.quickbooks.model.InvoiceItem;
import com.sasi.quickbooks.model.QuickBookHSNEnum;
import com.sasi.quickbooks.model.QuickBookInvoice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;

@Service
@Slf4j
public class QuickBookInvoiceService {

    @Autowired
    PDFGeneratorService pdfGeneratorService;

    final InvoiceRepository quickBookInvoiceRepository;

    final EntityManager em;

    public QuickBookInvoiceService(InvoiceRepository quickBookInvoiceRepository, EntityManager em) {
        this.quickBookInvoiceRepository = quickBookInvoiceRepository;
        this.em = em;
    }

    public void saveInvoice(QuickBookInvoice quickBookInvoice, HttpServletResponse response) throws DocumentException, IOException {
        setInvoiceForItem(quickBookInvoice);
        this.quickBookInvoiceRepository.save(quickBookInvoice);
        this.pdfGeneratorService.generateInvoiceFile(quickBookInvoice, response);
    }

    public File getInvoice() throws DocumentException, FileNotFoundException {
//       return pdfGeneratorService.generateInvoiceFile();
        return null;
    }

    private void setInvoiceForItem(QuickBookInvoice quickBookInvoice) {
        quickBookInvoice.getInvoiceItems()
                .forEach(invoiceItem -> invoiceItem.setQuickBookInvoice(quickBookInvoice));
    }

    public Integer getNextInvoiceNumber() {

        String invoiceNumberQuery = "select * from invoice_id_generator";
        Query invoiceTypedQuery = em.createNativeQuery(invoiceNumberQuery);
        BigInteger invoiceNumber = (BigInteger) invoiceTypedQuery.getSingleResult();
        return invoiceNumber.intValue();
    }
}
