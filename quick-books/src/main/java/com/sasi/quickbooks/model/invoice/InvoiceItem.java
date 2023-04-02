package com.sasi.quickbooks.model.invoice;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceItem {
    String descriptionOfItem;
    Float grossWeight;
    Float ratePerGram;
    Float amount;
}
