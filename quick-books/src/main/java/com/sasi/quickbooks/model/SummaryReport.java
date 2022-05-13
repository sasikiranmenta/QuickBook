package com.sasi.quickbooks.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class SummaryReport {
    private String type;
    private Float totalWeight;
    private Float totalBeforeTax;
    private Float totalCGST;
    private Float totalSGST;
    private Float totalAfterTax;
    private boolean isGstOnly;

    public SummaryReport() {
        this.totalAfterTax = 0f;
        this.totalBeforeTax = 0f;
        this.totalWeight = 0f;
        this.totalCGST = 0f;
        this.totalSGST = 0f;
    }
}
