package com.sasi.quickbooks.controller;

import com.itextpdf.text.DocumentException;
import com.sasi.quickbooks.model.InvoiceItem;
import com.sasi.quickbooks.model.QuickBookHSNEnum;
import com.sasi.quickbooks.model.QuickBookInvoice;
import com.sasi.quickbooks.service.QuickBookInvoiceService;
import com.sasi.quickbooks.service.SummaryReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("quick-book")
@CrossOrigin("*")
@Slf4j
public class InvoiceController {

    final QuickBookInvoiceService quickBookInvoiceService;

    final SummaryReportService summaryReportService;

    public InvoiceController(QuickBookInvoiceService quickBookInvoiceService, SummaryReportService summaryReportService) {
        this.quickBookInvoiceService = quickBookInvoiceService;
        this.summaryReportService = summaryReportService;
    }

    @RequestMapping(value = "/saveInvoice", method = RequestMethod.POST)
    public ResponseEntity saveInvoice(@RequestBody @Valid QuickBookInvoice quickBookInvoice, @RequestParam(name = "print") Boolean print,
                                      HttpServletRequest request, HttpServletResponse response) throws DocumentException, IOException {
        this.quickBookInvoiceService.saveInvoice(quickBookInvoice, print, response);
        if(print) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).build();
        }
        return ResponseEntity.ok().build();

    }

    @RequestMapping(value = "/getAllInvoiceInBetween", method = RequestMethod.GET)
    public ResponseEntity<List<QuickBookInvoice>> getAllInvoice(@RequestParam(name = "fromDate") String fromDate, @RequestParam(name = "toDate") String toDate ) {
        return ResponseEntity.ok(this.quickBookInvoiceService.getInvoicesInBetweenDatesBasedOnGst(fromDate, toDate, true, false));
    }

    @RequestMapping(value = "/getAllInvoiceInBetweenBasedOnGst", method = RequestMethod.GET)
    public ResponseEntity<List<QuickBookInvoice>> getAllInvoiceBasedOnGST(@RequestParam(name = "fromDate") String fromDate, @RequestParam(name = "toDate") String toDate,
                                                                          @RequestParam(name = "includeGst") Boolean includeGst, @RequestParam(name = "showOnlyGst") Boolean showOnlyGst) {
        return ResponseEntity.ok(this.quickBookInvoiceService.getInvoicesInBetweenDatesBasedOnGst(fromDate, toDate, includeGst, showOnlyGst));
    }

    @RequestMapping(value = "/getInvoiceNumber", method = RequestMethod.GET)
    public ResponseEntity<Integer> getInvoiceNumber() {
        return ResponseEntity.status(HttpStatus.OK).body(quickBookInvoiceService.getNextInvoiceNumber());
    }

    @RequestMapping(value = "/getInvoice", method = RequestMethod.GET)
    public ResponseEntity<QuickBookInvoice> getInvoiceBasedOnId(@RequestParam (name = "invoice_id") int invoiceId) {
        return ResponseEntity.ok(this.quickBookInvoiceService.getQuickBookBasedOnInvoiceId(invoiceId));
    }

    @RequestMapping(value = "/updateInvoice", method = RequestMethod.POST)
    public ResponseEntity updateInvoice(@RequestBody @Valid QuickBookInvoice invoice, @RequestParam(name = "print") Boolean print,
                                        HttpServletRequest request, HttpServletResponse response) throws DocumentException, IOException {
        this.quickBookInvoiceService.updateInvoice(invoice, print, response);
        if(print) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).build();
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/getBills", method = RequestMethod.POST)
    public ResponseEntity getBillsForInvoices(@RequestBody @Valid Set<Long> invoiceIds, HttpServletResponse response) throws DocumentException, IOException {
        this.quickBookInvoiceService.getBills(invoiceIds, response);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).build();
    }

    @RequestMapping(value = "/emailSummary", method = RequestMethod.GET)
    public ResponseEntity emailSummaryReport(@RequestParam(name = "fromDate") String fromDate, @RequestParam(name = "toDate") String toDate,
                                             @RequestParam(name = "emailId") String emailId) {
        if (this.summaryReportService.sendSummaryReport(fromDate, toDate, emailId)) {
           return ResponseEntity.ok().build();
        } else {
           return ResponseEntity.internalServerError().build();
        }
    }
}
