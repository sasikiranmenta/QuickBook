package com.sasi.quickbooks.model.config;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Document(collection = "invoiceSequenceConfig")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@TypeAlias("InvoiceSequenceConfig")
public class InvoiceSequenceConfig {

    @NotNull
    int nextInvoiceId;

    @Id
    Integer financialYear;

    boolean isBackedUp;
}
