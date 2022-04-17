package com.sasi.quickbooks.service;

import com.itextpdf.text.DocumentException;
import com.sasi.quickbooks.Repository.InvoiceRepository;
import com.sasi.quickbooks.model.QuickBookInvoice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

    public void saveInvoice(QuickBookInvoice quickBookInvoice, Boolean print, HttpServletResponse response) throws DocumentException, IOException {
        this.quickBookInvoiceRepository.save(quickBookInvoice);
        if(print) {
            this.pdfGeneratorService.generateInvoiceFile(quickBookInvoice, response);
        }
    }

    public File getInvoice() throws DocumentException, FileNotFoundException {
//       return pdfGeneratorService.generateInvoiceFile();
        return null;
    }


    public Integer getNextInvoiceNumber() {
        String invoiceNumberQuery = "select * from invoice_id_generator";
        Query invoiceTypedQuery = em.createNativeQuery(invoiceNumberQuery);
        BigInteger invoiceNumber = (BigInteger) invoiceTypedQuery.getSingleResult();
        return invoiceNumber.intValue();
    }

    public List<QuickBookInvoice> getInvoicesInBetweenDatesBasedOnGst(String fromDate, String toDate, Boolean includeGst, Boolean showonlyGst) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

        String inBetweenQuery = "select I from QuickBookInvoice I where I.billDate >= :fromDate and I.billDate <= :toDate";
        if(!includeGst) {
            inBetweenQuery = inBetweenQuery + " and I.gstin is null";
        }
        if(includeGst && showonlyGst) {
            inBetweenQuery = inBetweenQuery + " and I.gstin is not null";
        }

        TypedQuery<QuickBookInvoice> typedQuery = em.createQuery(inBetweenQuery, QuickBookInvoice.class);
        try {
            Calendar toCalendar = Calendar.getInstance();
            toCalendar.setTime(format.parse(toDate));
            toCalendar.add(Calendar.DAY_OF_WEEK, 1);
            toDate = format.format(toCalendar.getTime());
            typedQuery.setParameter("fromDate", format.parse(fromDate));
            typedQuery.setParameter("toDate", format.parse(toDate));
        } catch (ParseException e) {
            log.error(e.getMessage());
        }

        List<QuickBookInvoice> invoices = typedQuery.getResultList();
        return invoices;
    }

    public QuickBookInvoice getQuickBookBasedOnInvoiceId(int invoiceId) {

        String invoiceQuery = "select invoice from QuickBookInvoice invoice where invoice.invoiceId = :invoiceId";
        TypedQuery<QuickBookInvoice> typedQuery = em.createQuery(invoiceQuery, QuickBookInvoice.class);
        typedQuery.setParameter("invoiceId", Long.valueOf(invoiceId));
        QuickBookInvoice invoice = typedQuery.getSingleResult();
        return invoice;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public QuickBookInvoice updateInvoice(QuickBookInvoice invoice, Boolean print, HttpServletResponse response) throws DocumentException, IOException {
        String invoiceUpdateQuery = "select invoice from QuickBookInvoice invoice where invoice.invoiceId = :invoiceId";
        TypedQuery<QuickBookInvoice> typedQuery = em.createQuery(invoiceUpdateQuery, QuickBookInvoice.class);
        typedQuery.setParameter("invoiceId", invoice.getInvoiceId());
        QuickBookInvoice oldInvoice = typedQuery.getSingleResult();
        updateOldInvoice(oldInvoice, invoice);
        em.merge(oldInvoice);
        if(print) {
            this.pdfGeneratorService.generateInvoiceFile(invoice, response);
        }
        return oldInvoice;
    }

    private void updateOldInvoice(QuickBookInvoice oldInvoice, QuickBookInvoice invoice) {
        oldInvoice.setInvoiceType(invoice.getInvoiceType());
        oldInvoice.setCustomerName(invoice.getCustomerName());
        oldInvoice.setAddress(invoice.getAddress());
        oldInvoice.setState(invoice.getState());
        oldInvoice.setStateCode(invoice.getStateCode());
        oldInvoice.setBillDate(invoice.getBillDate());
        oldInvoice.setGstin(invoice.getGstin());
        oldInvoice.setInvoiceType(invoice.getInvoiceType());
        oldInvoice.getPaymentMode().clear();
        oldInvoice.getPaymentMode().addAll(invoice.getPaymentMode());
        oldInvoice.setAmountBeforeTax(invoice.getAmountBeforeTax());
        oldInvoice.setCgstAmount(invoice.getCgstAmount());
        oldInvoice.setSgstAmount(invoice.getSgstAmount());
        oldInvoice.setIgstAmount(invoice.getIgstAmount());
        oldInvoice.setTotalAmountAfterTax(invoice.getTotalAmountAfterTax());
        oldInvoice.setTotalWeight(invoice.getTotalWeight());
        oldInvoice.setPhoneNumber(invoice.getPhoneNumber());
        oldInvoice.setPaymentType(invoice.getPaymentType());
        oldInvoice.setQuickBookUpdatedTime(new Date());
        oldInvoice.getInvoiceItems().clear();
        oldInvoice.getInvoiceItems().addAll(invoice.getInvoiceItems());
    }

    public void getBills(Set<Long> invoiceIds, HttpServletResponse response) throws DocumentException, IOException {
        String multipleInvoiceQuery = "SELECT invoice from QuickBookInvoice invoice where invoice.invoiceId in :invoiceIds";
        TypedQuery<QuickBookInvoice> invoice = em.createQuery(multipleInvoiceQuery, QuickBookInvoice.class);
        invoice.setParameter("invoiceIds", invoiceIds);
        List<QuickBookInvoice> quickBookInvoices = invoice.getResultList();
        this.pdfGeneratorService.generateInvoices(quickBookInvoices, response);
    }
}
