package com.sasi.quickbooks.model.requestbody;

import com.sasi.quickbooks.model.huid.HuidItemStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class HuidRequestBody {
    Date from;
    Date to;
    HuidItemStatusEnum applyDateRangeOn;
    boolean includeSaledData;
    boolean includeStockData;
}
