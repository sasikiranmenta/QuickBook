package com.sasi.quickbooks.model.invoice;

import com.sasi.quickbooks.model.PaymentModeEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentMode {
    PaymentModeEnum paymentMode;
    Float amount;
}
