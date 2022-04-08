package com.sasi.quickbooks.controller;

import com.itextpdf.text.DocumentException;
import com.sasi.quickbooks.model.InvoiceItem;
import com.sasi.quickbooks.model.QuickBookHSNEnum;
import com.sasi.quickbooks.model.QuickBookInvoice;
import com.sasi.quickbooks.service.QuickBookInvoiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

@RestController
@RequestMapping("quick-book")
@CrossOrigin("*")
@Slf4j
public class InvoiceController {

    final QuickBookInvoiceService quickBookInvoiceService;

    public InvoiceController(QuickBookInvoiceService quickBookInvoiceService) {
        this.quickBookInvoiceService = quickBookInvoiceService;
    }

    @RequestMapping(value = "/saveInvoice", method = RequestMethod.POST)
    public ResponseEntity saveInvoice(@RequestBody @Valid QuickBookInvoice quickBookInvoice, HttpServletRequest request, HttpServletResponse response) throws DocumentException, IOException {
        this.quickBookInvoiceService.saveInvoice(quickBookInvoice, response);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).build();
    }

    @RequestMapping(value = "/getAllInvoiceInBetween", method = RequestMethod.GET)
    public ResponseEntity<List<QuickBookInvoice>> getAllInvoice(@RequestParam(name = "fromDate") String fromDate, @RequestParam(name = "toDate") String toDate ) {
        return ResponseEntity.ok(this.quickBookInvoiceService.getInvoicesInBetweenDates(fromDate, toDate));
    }

    @RequestMapping(value = "/getInvoiceNumber", method = RequestMethod.GET)
    public ResponseEntity<Integer> getInvoiceNumber() {
        return ResponseEntity.status(HttpStatus.OK).body(quickBookInvoiceService.getNextInvoiceNumber());
    }

    @RequestMapping(value = "/getInvoice", method = RequestMethod.GET)
    public ResponseEntity getInvoice(HttpServletRequest request, HttpServletResponse response) throws IOException, DocumentException {
        File invoicePdf = new File("D:\\QuickBooks\\HelloWorld-Table.pdf");
//        File invoicePdf = new File("D:\\passport_application.pdf");
//        response.setContentType("application/pdf");

//        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=users.pdf";
//        response.setHeader(headerKey, headerValue);
        if (invoicePdf != null && invoicePdf.exists()) {
            byte[] invoiceFile = Files.readAllBytes(invoicePdf.toPath());
            for (byte ch : invoiceFile) {
//                response.getWriter().append((char) ch);
//                log.info(String.valueOf((char)ch));
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(invoiceFile);
            }

//       QuickBookInvoice quickBookInvoice = new QuickBookInvoice();
//        quickBookInvoice.setAddress("Nellore");
//        quickBookInvoice.setInvoiceType(QuickBookHSNEnum.GOLD);
//        InvoiceItem item = new InvoiceItem();
//        item.setDescriptionOfItem("sell");
//        item.setGrossWeight(2f);
//        quickBookInvoice.setInvoiceItems(Arrays.asList(item));
//        return ResponseEntity.ok().body(quickBookInvoice);
        }
        return null;
    }
}
