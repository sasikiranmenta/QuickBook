package com.sasi.quickbooks.model.huid;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class HuidSummary {
    Date from;
    Date to;
    HuidSummaryDetail stockDetails;
    HuidSummaryDetail saleDetails;
}
