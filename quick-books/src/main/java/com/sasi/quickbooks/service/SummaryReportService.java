package com.sasi.quickbooks.service;

import com.sasi.quickbooks.QuickBookConstants;
import com.sasi.quickbooks.model.QuickBookHSNEnum;
import com.sasi.quickbooks.model.QuickBookInvoice;
import com.sasi.quickbooks.model.SummaryReport;
import com.sasi.quickbooks.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class SummaryReportService {

    final MailService mailService;
    final EntityManager em;
    final PDFGeneratorService pdfGeneratorService;

    public SummaryReportService(MailService mailService, EntityManager em, PDFGeneratorService pdfGeneratorService) {
        this.mailService = mailService;
        this.em = em;
        this.pdfGeneratorService = pdfGeneratorService;
    }

    public boolean sendSummaryReport(String fromDate, String toDate, String emailId) {
        Map<QuickBookConstants.SUMMARY_REPORT_ENUM, List<QuickBookInvoice>> summaryReportsMap = this.getSummaryReportsMap(fromDate, toDate);
        fromDate = DateUtil.convertFormatLetterFormat(fromDate, QuickBookConstants.FORMAT_MM_dd_yyyy);
        toDate = DateUtil.convertFormatLetterFormat(toDate, QuickBookConstants.FORMAT_MM_dd_yyyy);
        File withOutGstFile = pdfGeneratorService.generateSummaryReport(generateSummaryReport("Gold", summaryReportsMap.get(QuickBookConstants.SUMMARY_REPORT_ENUM.GOLD_NORMAL)),
                generateSummaryReport("Silver", summaryReportsMap.get(QuickBookConstants.SUMMARY_REPORT_ENUM.SILVER_NORMAL)), "noGst", fromDate, toDate);
        File onlyGstFile = pdfGeneratorService.generateSummaryReport(generateSummaryReport("Gold", summaryReportsMap.get(QuickBookConstants.SUMMARY_REPORT_ENUM.GOLD_GST)),
                generateSummaryReport("Silver", summaryReportsMap.get(QuickBookConstants.SUMMARY_REPORT_ENUM.SILVER_GST)), "onlyGst", fromDate, toDate);
         return mailService.sendMailWithAttachment(emailId, "Summary Report", "Summary Report from "+ fromDate + " to "+ toDate, withOutGstFile, onlyGstFile);
    }

    private Map<QuickBookConstants.SUMMARY_REPORT_ENUM, List<QuickBookInvoice>> getSummaryReportsMap(String fromDate, String toDate) {
        Map<QuickBookConstants.SUMMARY_REPORT_ENUM, List<QuickBookInvoice>> summaryReportsMap = new HashMap<>();
        summaryReportsMap.put(QuickBookConstants.SUMMARY_REPORT_ENUM.GOLD_NORMAL,
                this.getInvoiceInDateRangeBasedOnGstAndType(fromDate, toDate, QuickBookHSNEnum.GOLD, false));
        summaryReportsMap.put(QuickBookConstants.SUMMARY_REPORT_ENUM.SILVER_NORMAL,
                this.getInvoiceInDateRangeBasedOnGstAndType(fromDate, toDate, QuickBookHSNEnum.SILVER, false));
        summaryReportsMap.put(QuickBookConstants.SUMMARY_REPORT_ENUM.GOLD_GST,
                this.getInvoiceInDateRangeBasedOnGstAndType(fromDate, toDate, QuickBookHSNEnum.GOLD, true));
        summaryReportsMap.put(QuickBookConstants.SUMMARY_REPORT_ENUM.SILVER_GST,
                this.getInvoiceInDateRangeBasedOnGstAndType(fromDate, toDate, QuickBookHSNEnum.SILVER, true));
        return summaryReportsMap;
    }

    private List<QuickBookInvoice> getInvoiceInDateRangeBasedOnGstAndType(String fromDate, String toDate, QuickBookHSNEnum type, boolean includeGst) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

        String inBetweenQuery = "select I from QuickBookInvoice I where I.billDate >= :fromDate and I.billDate <= :toDate and I.invoiceType = :invoiceType";

        if (!includeGst) {
            inBetweenQuery = inBetweenQuery + " and I.gstin is null";
        } else {
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
            typedQuery.setParameter("invoiceType", type);
        } catch (ParseException e) {
            log.error(e.getMessage());
        }

        List<QuickBookInvoice> invoices = typedQuery.getResultList();
        if (invoices == null) {
            return new ArrayList<>();
        }
        return invoices;
    }

    private SummaryReport generateSummaryReport(String type, List<QuickBookInvoice> quickBookInvoices) {
        SummaryReport report = new SummaryReport();
        report.setType(type);
        quickBookInvoices.forEach((quickBookInvoice -> {
            report.setTotalAfterTax(report.getTotalAfterTax() + quickBookInvoice.getTotalAmountAfterTax());
            report.setTotalBeforeTax(report.getTotalBeforeTax() + quickBookInvoice.getAmountBeforeTax());
            report.setTotalCGST(report.getTotalCGST() + quickBookInvoice.getCgstAmount());
            report.setTotalSGST(report.getTotalSGST() + quickBookInvoice.getSgstAmount());
            report.setTotalWeight(report.getTotalWeight() + quickBookInvoice.getTotalWeight());
        }));
        return report;
    }
}
