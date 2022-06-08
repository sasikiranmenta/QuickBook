package com.sasi.quickbooks.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Component
@ToString
@EqualsAndHashCode
public class QuickBookInvoice implements Serializable {

    private static final long serialVersionUID = -2953998801387139748L;
    @Id
    @GeneratedValue(generator = "invoice_id_generator")
    @GenericGenerator(
            name = "invoice_id_generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "invoice_id_generator"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private long invoiceId;

    @Column(name = "CUSTOMER_NAME")
    private String customerName;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "STATE")
    private String state;

    @Column(name = "STATE_CODE")
    private String stateCode;

    @Column(name = "BILL_DATE")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date billDate;

    @Column(name = "GSTIN")
    private String gstin;

    @Column(name="GST_TYPE")
    @Enumerated(EnumType.STRING)
    private GstModeEnum gstType;

    @Column(name = "INVOICE_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private QuickBookHSNEnum invoiceType;

    @OneToMany( cascade = CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "INVOICE_ID", nullable = false)
    private Set<PaymentMode> paymentMode;

    @Column(name = "AMOUNT_BEFORE_TAX")
    private Float amountBeforeTax;

    @Column(name = "CGST_AMOUNT")
    private Float cgstAmount;

    @Column(name = "SGST_AMOUNT")
    private Float sgstAmount;

    @Column(name = "IGST_AMOUNT")
    private Float igstAmount;

    @Column(name = "TOTAL_AMOUNT_AFTER_TAX")
    private Float totalAmountAfterTax;

    @Column(name = "TOTAL_WEIGHT")
    private Float totalWeight;

    @Column(name = "PHONE_NUMBER")
    @Transient
    private String phoneNumber;

    @Column(name = "PAYMENT_TYPE")
    @Enumerated(EnumType.STRING)
    private PaymentTypeEnum paymentType;

    @Column(name = "QUICK_BOOK_UPDATE_TIME")
    @Transient
    private Date quickBookUpdatedTime;


    @OneToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "INVOICE_ID", nullable = false)
    private Set<InvoiceItem> invoiceItems;
}
