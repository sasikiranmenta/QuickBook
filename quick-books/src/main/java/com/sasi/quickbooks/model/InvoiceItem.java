package com.sasi.quickbooks.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.AccessType;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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

    private String invoiceItemId;

    @Column(name = "DESCRIPTION_OF_ITEM")
    private String descriptionOfItem;

    @Column(name = "GROSS_WEIGHT")
    private Float grossWeight;

    @Column(name = "RATE_PER_GRAM")
    private Float ratePerGram;

    @Column(name = "AMOUNT")
    private Float amount;

//    @ManyToOne
////    @JoinColumn(name = "invoice_id", nullable = false)
//    private QuickBookInvoice quickBookInvoice;

    @Id
    @Column(name = "INVOICE_ITEM_ID")
    @AccessType(AccessType.Type.PROPERTY)
    public String getInvoiceItemId() {
        if(this.invoiceItemId == null) {
            this.invoiceItemId = UUID.randomUUID().toString();
        }
        return this.invoiceItemId;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof InvoiceItem)) return false;
        final InvoiceItem other = (InvoiceItem) o;
        if (!other.canEqual(this)) return false;
        final Object this$invoiceItemId = this.getInvoiceItemId();
        final Object other$invoiceItemId = other.getInvoiceItemId();
        if (this$invoiceItemId == null ? other$invoiceItemId != null : !this$invoiceItemId.equals(other$invoiceItemId))
            return false;
        final Object this$descriptionOfItem = this.getDescriptionOfItem();
        final Object other$descriptionOfItem = other.getDescriptionOfItem();
        if (this$descriptionOfItem == null ? other$descriptionOfItem != null : !this$descriptionOfItem.equals(other$descriptionOfItem))
            return false;
        final Object this$grossWeight = this.getGrossWeight();
        final Object other$grossWeight = other.getGrossWeight();
        if (this$grossWeight == null ? other$grossWeight != null : !this$grossWeight.equals(other$grossWeight))
            return false;
        final Object this$ratePerGram = this.getRatePerGram();
        final Object other$ratePerGram = other.getRatePerGram();
        if (this$ratePerGram == null ? other$ratePerGram != null : !this$ratePerGram.equals(other$ratePerGram))
            return false;
        final Object this$amount = this.getAmount();
        final Object other$amount = other.getAmount();
        if (this$amount == null ? other$amount != null : !this$amount.equals(other$amount)) return false;
        return true;
    }

    protected boolean canEqual(final InvoiceItem other) {
        return other.getInvoiceItemId().equals(this.getInvoiceItemId());
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $invoiceItemId = this.getInvoiceItemId();
        result = result * PRIME + ($invoiceItemId == null ? 43 : $invoiceItemId.hashCode());
        final Object $descriptionOfItem = this.getDescriptionOfItem();
        result = result * PRIME + ($descriptionOfItem == null ? 43 : $descriptionOfItem.hashCode());
        final Object $grossWeight = this.getGrossWeight();
        result = result * PRIME + ($grossWeight == null ? 43 : $grossWeight.hashCode());
        final Object $ratePerGram = this.getRatePerGram();
        result = result * PRIME + ($ratePerGram == null ? 43 : $ratePerGram.hashCode());
        final Object $amount = this.getAmount();
        result = result * PRIME + ($amount == null ? 43 : $amount.hashCode());
        return result;
    }
}
