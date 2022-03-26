package com.sasi.quickbooks.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.AccessType;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@Component
@Entity
@AccessType(AccessType.Type.FIELD)
public class InvoiceItem implements Serializable {

    private static final long serialVersionUID = 4183996362210468257L;

    private String invoiceItemId = UUID.randomUUID().toString();

    @Column(name = "DESCRIPTION_OF_ITEM")
    private String descriptionOfItem;

    @Column(name = "GROSS_WEIGHT")
    private Float grossWeight;

    @Column(name = "RATE_PER_GRAM")
    private Float ratePerGram;

    @Column(name = "AMOUNT")
    private Float amount;

    @ManyToOne
    private QuickBookInvoice quickBookInvoice;

    @Id
    @Column(name = "INVOICE_ITEM_ID")
    @AccessType(AccessType.Type.PROPERTY)
    public String getInvoiceItemId() {
        return this.invoiceItemId;
    }
}
