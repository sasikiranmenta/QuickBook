package com.sasi.quickbooks.service;

import com.sasi.quickbooks.Repository.HuidRepository;
import com.sasi.quickbooks.model.huid.Huid;
import com.sasi.quickbooks.model.huid.HuidItemStatusEnum;
import com.sasi.quickbooks.model.requestbody.HuidRequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HuidService {

    private final HuidRepository repository;

    public void save(Huid huid) {
        repository.save(huid);
    }

    public Huid findByHuidNumber(String huidNumber) {
        return repository.findById(huidNumber).orElse(null);
    }

    public List<Huid> fetchAllHuid(HuidRequestBody requestBody) {
        if(requestBody.getApplyDateRangeOn() == HuidItemStatusEnum.INSTOCK) {
            if(requestBody.isIncludeSaledData() && requestBody.isIncludeStockData()) {
                return repository.fetchHuidBasedOnStock(requestBody.getFrom(), requestBody.getTo());
            } else if(requestBody.isIncludeSaledData()) {
                return repository.fetchHuidBasedOnStockIncludeOnlySale(requestBody.getFrom(), requestBody.getTo());
            }
            return repository.fetchHuidBasedOnStockIncludeOnlyStock(requestBody.getFrom(), requestBody.getTo());

        }
        else {
            if(requestBody.isIncludeSaledData() && requestBody.isIncludeStockData()) {
                return repository.fetchHuidBasedOnSale(requestBody.getFrom(), requestBody.getTo());
            } else if(requestBody.isIncludeSaledData()) {
                return repository.fetchHuidBasedOnSaleIncludeOnlySale(requestBody.getFrom(), requestBody.getTo());
            }
            return repository.fetchHuidBasedOnSaleIncludeOnlyStock(requestBody.getFrom(), requestBody.getTo());
        }
    }
}
