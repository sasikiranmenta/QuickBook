package com.sasi.quickbooks.service;

import com.sasi.quickbooks.QuickBookConstants;
import com.sasi.quickbooks.Repository.InvoiceRepository;
import com.sasi.quickbooks.model.invoice.Invoice;
import com.sasi.quickbooks.model.QuickBookHSNEnum;
import com.sasi.quickbooks.model.SummaryReport;
import com.sasi.quickbooks.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SummaryReportService {

    final MailService mailService;
    final QuickBookInvoiceService invoiceService;
    final PDFGeneratorService pdfGeneratorService;
    final InvoiceRepository invoiceRepository;

    public boolean sendSummaryReport(String fromDate, String toDate, String emailId) throws ParseException {
        Map<QuickBookConstants.SUMMARY_REPORT_ENUM, List<Invoice>> summaryReportsMap = this.getSummaryReportsMap(fromDate, toDate);
        fromDate = DateUtil.convertFormatLetterFormat(fromDate, QuickBookConstants.FORMAT_MM_dd_yyyy);
        toDate = DateUtil.convertFormatLetterFormat(toDate, QuickBookConstants.FORMAT_MM_dd_yyyy);
        File withOutGstFile = pdfGeneratorService.generateSummaryReport(generateSummaryReport("Gold", summaryReportsMap.get(QuickBookConstants.SUMMARY_REPORT_ENUM.GOLD_NORMAL)),
                generateSummaryReport("Silver", summaryReportsMap.get(QuickBookConstants.SUMMARY_REPORT_ENUM.SILVER_NORMAL)), "noGst", fromDate, toDate);
        File onlyGstFile = pdfGeneratorService.generateSummaryReport(generateSummaryReport("Gold", summaryReportsMap.get(QuickBookConstants.SUMMARY_REPORT_ENUM.GOLD_GST)),
                generateSummaryReport("Silver", summaryReportsMap.get(QuickBookConstants.SUMMARY_REPORT_ENUM.SILVER_GST)), "onlyGst", fromDate, toDate);
        return mailService.sendMailWithAttachment(emailId, "Summary Report from " + fromDate + " to " + toDate, "", withOutGstFile, onlyGstFile);
    }

    private Map<QuickBookConstants.SUMMARY_REPORT_ENUM, List<Invoice>> getSummaryReportsMap(String fromDate, String toDate) throws ParseException {
        Map<QuickBookConstants.SUMMARY_REPORT_ENUM, List<Invoice>> summaryReportsMap = new HashMap<>();
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


    private List<Invoice> getInvoiceInDateRangeBasedOnGstAndType(String fromDate, String toDate, QuickBookHSNEnum type, boolean includeGst) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(format.parse(toDate));
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        List<Invoice> invoices = null;
        if (!includeGst) {
            invoices = invoiceRepository.invoiceByBillDateBetweenNoGSTBasedOnType(format.parse(fromDate), calendar.getTime(), type);
        } else {
            invoices = invoiceRepository.invoiceByBillDateBetweenOnlyGSTBasedOnType(format.parse(fromDate), calendar.getTime(), type);
        }


        if (invoices == null) {
            return new ArrayList<>();
        }
        return invoices;
    }

    private SummaryReport generateSummaryReport(String type, List<Invoice> quickBookInvoices) {
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
