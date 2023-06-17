package com.sasi.quickbooks.model.huid;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class HuidSummary {
    Date from;
    Date to;
    List<HuidSummaryDetail> details;
}
