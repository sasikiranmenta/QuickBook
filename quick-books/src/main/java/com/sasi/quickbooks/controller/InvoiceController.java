package com.sasi.quickbooks.controller;

import com.itextpdf.text.DocumentException;
import com.sasi.quickbooks.dto.InvoiceIDDto;
import com.sasi.quickbooks.model.invoice.Invoice;
import com.sasi.quickbooks.service.InvoiceConfigService;
import com.sasi.quickbooks.service.QuickBookInvoiceService;
import com.sasi.quickbooks.service.SummaryReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@RequestMapping("quick-book")
@CrossOrigin("*")
@Slf4j
@RequiredArgsConstructor
public class InvoiceController {

    final QuickBookInvoiceService quickBookInvoiceService;

    final SummaryReportService summaryReportService;

    final InvoiceConfigService configService;


    @RequestMapping(value = "/saveInvoice", method = RequestMethod.POST)
    public ResponseEntity saveInvoice(@RequestBody @Valid Invoice quickBookInvoice, @RequestParam(name = "print") Boolean print,
                                      HttpServletResponse response) throws DocumentException, IOException {
        this.quickBookInvoiceService.saveInvoiceMongo(quickBookInvoice, print, response);
        if (print) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).build();
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/getAllInvoiceInBetween", method = RequestMethod.GET)
    public ResponseEntity<List<Invoice>> getAllInvoice(@RequestParam(name = "fromDate") String fromDate, @RequestParam(name = "toDate") String toDate) throws ParseException {
        return ResponseEntity.ok(this.quickBookInvoiceService.getInvoicesInBetweenDatesBasedOnGst(fromDate, toDate, true, false));
    }

    @RequestMapping(value = "/getAllInvoiceInBetweenBasedOnGst", method = RequestMethod.GET)
    public ResponseEntity<List<Invoice>> getAllInvoiceBasedOnGST(@RequestParam(name = "fromDate") String fromDate, @RequestParam(name = "toDate") String toDate,
                                                                 @RequestParam(name = "includeGst") Boolean includeGst, @RequestParam(name = "showOnlyGst") Boolean showOnlyGst) throws ParseException {
        return ResponseEntity.ok(this.quickBookInvoiceService.getInvoicesInBetweenDatesBasedOnGst(fromDate, toDate, includeGst, showOnlyGst));
    }

    @RequestMapping(value = "/getInvoiceNumber", method = RequestMethod.GET)
    public ResponseEntity<Integer> getInvoiceNumber(@RequestParam(name = "billDate") String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return ResponseEntity.status(HttpStatus.OK).body(configService.getNextInvoiceNumber(dateFormat.parse(date)));
    }

    @RequestMapping(value = "/getInvoice", method = RequestMethod.GET)
    public ResponseEntity<Invoice> getInvoiceBasedOnId(@RequestParam(name = "invoice_id") int invoiceId, @RequestParam(name = "financialYear") int financialYear) {
        return ResponseEntity.ok(this.quickBookInvoiceService.getQuickBookBasedOnInvoiceId(invoiceId, financialYear));
    }

    @RequestMapping(value = "/updateInvoice", method = RequestMethod.POST)
    public ResponseEntity updateInvoice(@RequestBody @Valid Invoice invoice, @RequestParam(name = "print") Boolean print,
                                        HttpServletResponse response) throws DocumentException, IOException {
        this.quickBookInvoiceService.updateInvoice(invoice, print, response);
        if (print) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).build();
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/getBills", method = RequestMethod.POST)
    public ResponseEntity getBillsForInvoices(@RequestBody @Valid List<InvoiceIDDto> invoiceIds, HttpServletResponse response) throws DocumentException, IOException {
        this.quickBookInvoiceService.getBills(invoiceIds, response);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).build();
    }

    @RequestMapping(value = "/emailSummary", method = RequestMethod.GET)
    public ResponseEntity emailSummaryReport(@RequestParam(name = "fromDate") String fromDate, @RequestParam(name = "toDate") String toDate,
                                             @RequestParam(name = "emailId") String emailId) throws ParseException {
        if (this.summaryReportService.sendSummaryReport(fromDate, toDate, emailId)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }
}
