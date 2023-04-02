package com.sasi.quickbooks.service;

import com.itextpdf.text.DocumentException;
import com.sasi.quickbooks.Repository.InvoiceRepository;
import com.sasi.quickbooks.dto.InvoiceIDDto;
import com.sasi.quickbooks.mapper.InvoiceMapper;
import com.sasi.quickbooks.model.invoice.Invoice;
import com.sasi.quickbooks.util.InvoiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuickBookInvoiceService {

    final PDFGeneratorService pdfGeneratorService;
    final InvoiceConfigService invoiceConfigService;
    final InvoiceRepository invoiceRepository;
    final InvoiceMapper invoiceMapper;
    final SimpMessagingTemplate template;

    public void saveInvoiceMongo(Invoice quickBookInvoice, Boolean print, HttpServletResponse response) throws DocumentException, IOException {
        quickBookInvoice.setFinancialYear(InvoiceUtil.getFinancialYear(quickBookInvoice.getBillDate()));
        this.invoiceRepository.save(quickBookInvoice);
        this.invoiceConfigService.incrementInvoiceId(quickBookInvoice.getBillDate());
        if (print) {
            this.pdfGeneratorService.generateInvoiceFile(quickBookInvoice, response);
        }
    }

    public List<Invoice> getInvoicesInBetweenDatesBasedOnGst(String fromDate, String toDate, Boolean includeGst, Boolean showonlyGst) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(format.parse(toDate));
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        if (!includeGst) {
            return this.invoiceRepository.invoiceByBillDateBetweenNoGST(format.parse(fromDate), calendar.getTime());
        }
        if (includeGst && showonlyGst) {
            return this.invoiceRepository.invoiceByBillDateBetweenOnlyGST(format.parse(fromDate), calendar.getTime());
        }
        return this.invoiceRepository.invoiceByBillDateBetween(format.parse(fromDate), calendar.getTime());

    }

    @Scheduled(fixedRate = 1000)
    private void networkStatusCheck() {
        try {
            InetAddress inetAddress = InetAddress.getByName("www.google.com");
            boolean isReachable = inetAddress.isReachable(3000); // 5000 milliseconds timeout
            if (isReachable) {
                System.out.println("Network connection is available");
                template.convertAndSend("/topic", true);
                return;
            }
        } catch (Exception e) {
            System.out.println("Error checking network connection: " + e.getMessage());
        }
        System.out.println("Network connection is not available");
        template.convertAndSend("/topic", false);
    }

    public Invoice getQuickBookBasedOnInvoiceId(int invoiceId, int financialYear) {
        return this.invoiceRepository.getMongoInvoiceByInvoiceIdAndFinancialYear(invoiceId, financialYear);
    }

    public Invoice updateInvoice(Invoice invoice, Boolean print, HttpServletResponse response) throws DocumentException, IOException {
        invoice.setFinancialYear(InvoiceUtil.getFinancialYear(invoice.getBillDate()));
        Invoice oldInvoice = this.invoiceRepository.getMongoInvoiceByInvoiceIdAndFinancialYear(invoice.getInvoiceId(), invoice.getFinancialYear());
        invoiceMapper.map(invoice, oldInvoice);
        this.invoiceRepository.save(oldInvoice);
        if (print) {
            this.pdfGeneratorService.generateInvoiceFile(invoice, response);
        }
        return oldInvoice;
    }

    public void getBills(List<InvoiceIDDto> invoiceIds, HttpServletResponse response) throws DocumentException, IOException {
        List<Invoice> quickBookInvoices = new ArrayList<>();
        invoiceIds.forEach(invoiceIDDto ->
                quickBookInvoices.add(this.invoiceRepository.getMongoInvoiceByInvoiceIdAndFinancialYear(invoiceIDDto.getInvoiceId(), invoiceIDDto.getFinancialYear())));
        this.pdfGeneratorService.generateInvoices(quickBookInvoices, response);
    }
}
