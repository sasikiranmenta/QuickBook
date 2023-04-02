package com.sasi.quickbooks.service;

import com.sasi.quickbooks.Repository.InvoiceSequenceConfigRepository;
import com.sasi.quickbooks.model.config.InvoiceSequenceConfig;
import com.sasi.quickbooks.util.InvoiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;


@Service
@RequiredArgsConstructor
public class InvoiceConfigService {
    private static final Integer START_SEQUENCE_ID = 1;
    private final InvoiceSequenceConfigRepository sequenceConfigRepository;

    public Integer getNextInvoiceNumber(Date date) {
        Integer financialYear = InvoiceUtil.getFinancialYear(date);
        return sequenceConfigRepository.findById(financialYear)
                .map(InvoiceSequenceConfig::getNextInvoiceId)
                .orElseGet(() -> createNewSequenceForYear(financialYear));
    }

    public void incrementInvoiceId(Date date) {
        Integer financialYear = InvoiceUtil.getFinancialYear(date);
        sequenceConfigRepository.findById(financialYear)
                .map(invoiceSequenceConfig -> {
                    invoiceSequenceConfig.setNextInvoiceId(invoiceSequenceConfig.getNextInvoiceId() + 1);
                    invoiceSequenceConfig.setBackedUp(false);
                    sequenceConfigRepository.save(invoiceSequenceConfig);
                    return true;
                }).orElseGet(() -> !ObjectUtils.isEmpty(createNewSequenceForYear(financialYear)));
    }

    private Integer createNewSequenceForYear(Integer financialYear) {
        return sequenceConfigRepository.save(
                        InvoiceSequenceConfig
                                .builder()
                                .nextInvoiceId(START_SEQUENCE_ID)
                                .financialYear(financialYear)
                                .build())
                .getNextInvoiceId();
    }
}
