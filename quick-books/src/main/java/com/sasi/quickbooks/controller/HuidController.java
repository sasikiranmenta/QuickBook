package com.sasi.quickbooks.controller;

import com.itextpdf.text.DocumentException;
import com.sasi.quickbooks.model.huid.Huid;
import com.sasi.quickbooks.model.huid.HuidResponse;
import com.sasi.quickbooks.model.requestbody.HuidRequestBody;
import com.sasi.quickbooks.service.HuidService;
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
    public ResponseEntity<Huid> getByHuidNumber(@RequestParam(name = "huidNumber") String number) throws ParseException {
        return ResponseEntity.status(HttpStatus.OK).body(huidService.findByHuidNumber(number));
    }


    @RequestMapping(value = "/downloadData", method = RequestMethod.POST)
    public ResponseEntity getBillsForInvoices(@RequestBody HuidRequestBody requestBody, HttpServletResponse response) throws DocumentException, IOException {
        this.huidService.downloadHuidData(requestBody, response);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).build();
    }

    @RequestMapping(value = "/fetchAllHuid", method = RequestMethod.POST)
    public HuidResponse getAllHuid(@RequestBody HuidRequestBody requestBody) {
        return this.huidService.fetchAllHuid(requestBody);
    }

    @RequestMapping(value = "/emailSummary", method = RequestMethod.POST)
    public ResponseEntity emailSummaryReport(@RequestBody HuidRequestBody requestBody, @RequestParam(name = "emailId") String emailId) throws ParseException {
        if (this.huidService.sendHuidEmail(requestBody, emailId)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }
}
