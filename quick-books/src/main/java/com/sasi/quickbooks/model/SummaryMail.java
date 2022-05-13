package com.sasi.quickbooks.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class SummaryMail implements Serializable {
    private static final long serialVersionUID = -5184928117768691560L;
    private String fromDate;
    private String toDate;
    private String emailIds;
}
