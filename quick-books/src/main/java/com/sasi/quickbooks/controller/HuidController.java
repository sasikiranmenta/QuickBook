package com.sasi.quickbooks.controller;

import com.itextpdf.text.DocumentException;
import com.sasi.quickbooks.dto.InvoiceIDDto;
import com.sasi.quickbooks.model.huid.Huid;
import com.sasi.quickbooks.model.invoice.Invoice;
import com.sasi.quickbooks.service.HuidService;
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
@RequestMapping("quick-book/huid")
@CrossOrigin("*")
@Slf4j
@RequiredArgsConstructor
public class HuidController {

    final HuidService huidService;

    @RequestMapping(value = "/saveHuid", method = RequestMethod.POST)
    public ResponseEntity saveInvoice(@RequestBody @Valid Huid huid) throws DocumentException, IOException {
        this.huidService.save(huid);
        return ResponseEntity.ok().build();
    }


    @RequestMapping(value = "/getByHuidNumber", method = RequestMethod.GET)
    public ResponseEntity<Huid> getInvoiceNumber(@RequestParam(name = "huidNumber") String number) throws ParseException {
        return ResponseEntity.status(HttpStatus.OK).body(huidService.findByHuidNumber(number));
    }

//    @RequestMapping(value = "/updateInvoice", method = RequestMethod.POST)
//    public ResponseEntity updateInvoice(@RequestBody @Valid Invoice invoice, @RequestParam(name = "print") Boolean print,
//                                        HttpServletResponse response) throws DocumentException, IOException {
//        this.quickBookInvoiceService.updateInvoice(invoice, print, response);
//        if (print) {
//            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).build();
//        }
//        return ResponseEntity.ok().build();
//    }

    @RequestMapping(value = "/fetchAllHuid", method = RequestMethod.GET)
    public List<Huid> getAllHuid() {
        return this.huidService.fetchAllHuid();
    }

//    @RequestMapping(value = "/emailSummary", method = RequestMethod.GET)
//    public ResponseEntity emailSummaryReport(@RequestParam(name = "fromDate") String fromDate, @RequestParam(name = "toDate") String toDate,
//                                             @RequestParam(name = "emailId") String emailId) throws ParseException {
//        if (this.summaryReportService.sendSummaryReport(fromDate, toDate, emailId)) {
//            return ResponseEntity.ok().build();
//        } else {
//            return ResponseEntity.internalServerError().build();
//        }
//    }
}
