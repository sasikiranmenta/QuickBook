package com.sasi.quickbooks.model.invoice;

import com.sasi.quickbooks.model.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Set;

@Document("invoice")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@TypeAlias("Invoice")
public class Invoice {
    @MongoId
    ObjectId id;

    @Indexed
    int invoiceId;
    String customerName;
    String address;
    String state;
    String stateCode;

    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date billDate;
    int financialYear;
    String identificationNumber;
    IdentificationType identificationNumberType;
    QuickBookHSNEnum invoiceType;
    Set<PaymentMode> paymentMode;
    Float amountBeforeTax;
    Float cgstAmount;
    Float sgstAmount;
    Float igstAmount;
    Float totalAmountAfterTax;
    Float totalWeight;
    String phoneNumber;
    PaymentTypeEnum paymentType;
    Date quickBookUpdated;
    Set<InvoiceItem> invoiceItems;
    boolean isBackedUp;
}
