package com.sasi.quickbooks.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.AccessType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "PAYMENT_MODE")
@Getter
@Setter
@NoArgsConstructor
@AccessType(AccessType.Type.FIELD)
public class PaymentMode implements Serializable {

    private static final long serialVersionUID = -784976737109738770L;

    private String paymentModeId = UUID.randomUUID().toString();

    @Column(name = "PAYMENT_MODE")
    @Enumerated(EnumType.STRING)
    private PaymentModeEnum paymentMode;

    @Column(name = "AMOUNT")
    private Float amount;

    @ManyToOne
    private QuickBookInvoice quickBookInvoice;

    @Id
    @Column(name = "PAYMENT_MODE_ID")
    @AccessType(AccessType.Type.PROPERTY)
    public String getPaymentModeId() {
        return this.paymentModeId;
    }
}
