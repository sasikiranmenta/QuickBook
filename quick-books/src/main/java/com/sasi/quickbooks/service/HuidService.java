package com.sasi.quickbooks.service;

import com.itextpdf.text.DocumentException;
import com.sasi.quickbooks.QuickBookConstants;
import com.sasi.quickbooks.Repository.HuidRepository;
import com.sasi.quickbooks.model.ItemTypeEnum;
import com.sasi.quickbooks.model.huid.*;
import com.sasi.quickbooks.model.requestbody.HuidRequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.sasi.quickbooks.util.DateUtil.convertFormatLetterFormat;

@Service
@RequiredArgsConstructor
public class HuidService {

    private final HuidRepository repository;
    private final HuidPdfGeneratorService pdfGeneratorService;
    private final MailService mailService;

    public void save(Huid huid) {
        repository.save(huid);
    }

    public Huid findByHuidNumber(String huidNumber) {
        return repository.findById(huidNumber).orElse(null);
    }

    public HuidResponse fetchAllHuid(HuidRequestBody requestBody) {
        List<Huid> data = new ArrayList<>();
        if (requestBody.getApplyDateRangeOn() == HuidItemStatusEnum.INSTOCK) {
            if (requestBody.isIncludeSaledData() && requestBody.isIncludeStockData()) {
                data = repository.fetchHuidBasedOnStock(requestBody.getFrom(), requestBody.getTo());
            } else if (requestBody.isIncludeSaledData()) {
                data = repository.fetchHuidBasedOnStockIncludeOnlySale(requestBody.getFrom(), requestBody.getTo());
            } else {
                data = repository.fetchHuidBasedOnStockIncludeOnlyStock(requestBody.getFrom(), requestBody.getTo());
            }
        } else {
            if (requestBody.isIncludeSaledData() && requestBody.isIncludeStockData()) {
                data = repository.fetchHuidBasedOnSale(requestBody.getFrom(), requestBody.getTo());
            } else if (requestBody.isIncludeSaledData()) {
                data = repository.fetchHuidBasedOnSaleIncludeOnlySale(requestBody.getFrom(), requestBody.getTo());
            } else {
                data = repository.fetchHuidBasedOnSaleIncludeOnlyStock(requestBody.getFrom(), requestBody.getTo());
            }
        }

        return HuidResponse.builder()
                .data(data)
                .summary(generateHuidSummary(requestBody.getFrom(), requestBody.getTo(), data))
                .build();
    }

    private HuidSummary generateHuidSummary(Date from, Date to, List<Huid> data) {
        HuidSummaryDetail stockDetails = new HuidSummaryDetail();
        HuidSummaryDetail saleDetails = new HuidSummaryDetail();
        data.forEach(huid -> {
            if (huid.isSaled()) {
                if (huid.getItemType() == ItemTypeEnum.GOLD) {
                    saleDetails.incrementGoldWeight(huid.getGrossWeight());
                    saleDetails.incrementGoldItems();
                } else {
                    saleDetails.incrementSilverWeight(huid.getGrossWeight());
                    saleDetails.incrementSilverItems();
                }
            } else {
                if (huid.getItemType() == ItemTypeEnum.GOLD) {
                    stockDetails.incrementGoldWeight(huid.getGrossWeight());
                    stockDetails.incrementGoldItems();
                } else {
                    stockDetails.incrementSilverWeight(huid.getGrossWeight());
                    stockDetails.incrementSilverItems();
                }
            }
        });

        return HuidSummary.builder().from(from)
                .to(to)
                .stockDetails(stockDetails)
                .saleDetails(saleDetails)
                .build();
    }

    public void downloadHuidData(HuidRequestBody requestBody, HttpServletResponse response) throws DocumentException, IOException {
        this.pdfGeneratorService.generateHuidFile(fetchAllHuid(requestBody), response, requestBody);
    }


    public boolean sendHuidEmail(HuidRequestBody requestBody, String emailId) {
        File huidFile = pdfGeneratorService.generateHuidFileForDownload(fetchAllHuid(requestBody), requestBody);
        return mailService.sendMailWithAttachment(emailId, "HUID report from " + convertFormatLetterFormat(requestBody.getFrom(), QuickBookConstants.FORMAT_MM_dd_yyyy) + " to " + convertFormatLetterFormat(requestBody.getTo(), QuickBookConstants.FORMAT_MM_dd_yyyy), "", huidFile, null);
    }
}
