import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ModalController} from '@ionic/angular';
import {ItemComponent} from './item/item.component';
import {Item} from './item/item';
import {formatDate} from '@angular/common';
import {Invoice, PaymentMode} from './invoice';
import {InvoiceService} from '../services/invoice.service';
import {PaymentModeComponent} from './payment-mode/payment-mode.component';
import {ActivatedRoute, Route, Router} from '@angular/router';

@Component({
    selector: 'app-invoice', templateUrl: './invoice.page.html', styleUrls: ['./invoice.page.scss'],
})
export class InvoicePage implements OnInit {
    invoiceForm: FormGroup;
    itemDetailsArray: Array<Item>;
    invoiceId = '';
    isInvalid: boolean;
    isMulti: boolean;
    paymentModeList: Array<PaymentMode>;
    paymentModeSetAmount: number;
    totalBillAmount: number;
    showErrorDiv = false;
    isLoading = false;
    errorMessage: string;
    isCredit: boolean;
    isEditMode: boolean;
    reverseBilling = false;

    constructor(private modalController: ModalController,
                private invoiceService: InvoiceService,
                private routerSnapshot: ActivatedRoute,
                private router: Router) {
    }

    ngOnInit(){
        const id = this.routerSnapshot.snapshot.params.id;
        if(!!id) {
            this.isEditMode = true;
        } else {
            this.isEditMode = false;
        }
        this.isLoading = true;
        this.itemDetailsArray = new Array<Item>();
        this.paymentModeList = new Array<PaymentMode>();
        this.isInvalid = false;
        this.isMulti = false;
        this.totalBillAmount = 0;
        this.isCredit = false;
        this.errorMessage = '';
        this.paymentModeSetAmount = 0;
        this.reverseBilling = false;
        this.initForm(id);
    }

    ionViewWillEnter() {
        this.ngOnInit();
    }

    onSubmit(print = true) {
        const paymentMode = this.invoiceForm.controls.paymentMode.value;
        if (this.invoiceForm.invalid || this.itemDetailsArray.length === 0) {
            this.isInvalid = true;
            return;
        } else if(!this.isCredit && paymentMode === 'MULTI' && this.paymentModeList.length === 0) {
            this.setErrorDiv('Set Amounts in Partial Payment Screen before saving');
            this.onShowPaymentMode();
            return;
        } else if (paymentMode === 'MULTI' && this.paymentModeSetAmount !== this.totalBillAmount) {
            this.setErrorDiv('Payment mode amount not matched');
            this.onShowPaymentMode();
            return;
        }
        this.saveIntoDb(print);
    }

    onAddItem() {
        this.modalController.create({
            component: ItemComponent, componentProps: {selectedMode: 'add'}, backdropDismiss: false
        })
            .then(modalElement => {
                modalElement.present();
                return modalElement.onDidDismiss();
            }).then(resultData => {
            if (resultData.role === 'confirm') {
                this.itemDetailsArray.push(resultData.data.invoiceItem);
                this.setSummaryDetails();
            }
        });
    }

    editItem(i: number) {
        this.reverseBilling = false;
        this.modalController.create({
            backdropDismiss: false,
            component: ItemComponent,
            componentProps: {isEditMode: true, editItemData: this.itemDetailsArray[i]}
        })
            .then(modalElement => {
                modalElement.present();
                return modalElement.onDidDismiss();
            }).then(resultData => {
            if (resultData.role === 'confirm') {
                this.itemDetailsArray[i] = resultData.data.invoiceItem;
                this.setSummaryDetails();
            }
        });
    }

    deleteItem(i: number) {
        this.itemDetailsArray.splice(i, 1);
        this.setSummaryDetails();
    }

    getInvoiceNumber() {
        this.invoiceService.getInvoiceNumber().subscribe((invoiceId) => {
            this.invoiceId = invoiceId;
            this.isLoading = false;
        });
    }

    setSummaryDetails() {
        let totalAmountBeforeTax = 0;
        let cgst = 0;
        let sgst = 0;
        let totalAmountAfterTax = 0;
        this.itemDetailsArray.forEach((item) => {
            totalAmountBeforeTax += item.amount;
        });

        cgst = Math.round(totalAmountBeforeTax * 1.5 / 100);
        sgst = Math.round(totalAmountBeforeTax * 1.5 / 100);
        totalAmountAfterTax = Math.round(totalAmountBeforeTax + cgst + sgst);
        this.totalBillAmount = totalAmountAfterTax;
        this.invoiceForm.controls.amountBeforeTax.setValue(totalAmountBeforeTax);
        this.invoiceForm.controls.cgstAmount.setValue(cgst);
        this.invoiceForm.controls.sgstAmount.setValue(sgst);
        this.invoiceForm.controls.totalAmountAfterTax.setValue(totalAmountAfterTax);
    }

    onPaymentModeChange() {
        if(this.invoiceForm.controls.paymentMode.value === 'MULTI') {
            this.isMulti = true;
        } else {
            this.isMulti = false;
            this.paymentModeList = [];
        }
    }

    onShowPaymentMode() {
        if(this.itemDetailsArray.length === 0) {
            this.setErrorDiv('Add at-least one item to set partial payment');
            return;
        }
        this.modalController.create({
            component: PaymentModeComponent,
            componentProps: {
                paymentModeList: this.paymentModeList,
                totalBillAmount: this.totalBillAmount
            }
        }).then((modalElement) => {
            modalElement.present();
            return modalElement.onDidDismiss();
        }).then((paymentModeResponse) => {
            if(paymentModeResponse.role === 'success') {
                this.paymentModeList = paymentModeResponse.data.list;
                this.paymentModeSetAmount = paymentModeResponse.data.paymentAmount;
            }
        });
    }


    onPaymentTypeChanged() {
        this.isCredit = !this.isCredit;
        if(!this.isCredit) {
            this.invoiceForm.controls.paymentMode.setValue('CASH');
        } else {
            this.invoiceForm.controls.paymentMode.setValue('NA');
        }
    }

    onCancel() {

    }

    onCancelEditingMode() {
        this.router.navigateByUrl('/invoice');
    }

    initiateReverseBill() {
        if(this.reverseBilling) {
            const total = this.invoiceForm.controls.totalAmountAfterTax.value;
            let three = total - total / 1.03;
            three = Math.round(three / 2) + Math.round(three / 2);
            this.itemDetailsArray = new Array<Item>();
            this.itemDetailsArray.push({
                descriptionOfItem: '',
                grossWeight: undefined,
                ratePerGram: undefined,
                amount: Math.floor(total - three) + three === total ? Math.floor(total - three) : Math.ceil(total - three)
            });
            this.editItem(0);
        }
    }

    private saveIntoDb(isPrint: boolean) {
        const invoice: Invoice = this.invoiceForm.value;
        if(!invoice.gstin) {
            console.log('hi');
            invoice.gstType = undefined;
            console.log(invoice);
        }
        invoice.invoiceItems = this.itemDetailsArray;
        invoice.amountBeforeTax = this.invoiceForm.controls.amountBeforeTax.value;
        invoice.cgstAmount = this.invoiceForm.controls.cgstAmount.value;
        invoice.sgstAmount = this.invoiceForm.controls.sgstAmount.value;
        invoice.igstAmount = this.invoiceForm.controls.igstAmount.value;
        invoice.totalAmountAfterTax = this.invoiceForm.controls.totalAmountAfterTax.value;
        invoice.totalWeight = this.getTotalWeight(invoice.invoiceItems);
        invoice.paymentMode = this.getPaymentModeDetails();
        if(this.isEditMode) {
            invoice.invoiceId = +this.invoiceId;
        }


        this.invoiceService.persistInvoice(invoice, isPrint, this.isEditMode)
            .subscribe((response) => {
                if (isPrint) {
                    this.invoiceService.downloadPDF(response);
                }
                this.router.navigateByUrl('/invoice');
                this.ngOnInit();
            });
    }

    private initForm(id: string) {
        let paymentMode = 'CASH';
        let tempInvoice: Invoice = {
            invoiceItems: undefined,
            paymentMode: undefined,
            totalWeight: 0,
            customerName: undefined,
            address: 'Nellore',
            state: 'AP',
            stateCode: 37,
            gstin: undefined,
            billDate: new Date(),
            amountBeforeTax: 0,
            paymentType: 'CASH',
            cgstAmount: 0,
            sgstAmount: 0,
            igstAmount: 0,
            invoiceType: 'GOLD',
            totalAmountAfterTax: 0,
            phoneNumber: undefined,
            invoiceId: undefined,
            gstType: 'PAN'
        };
        if (!!id) {
            this.invoiceService.getInvoice(+id).subscribe((invoice: Invoice) => {
                this.isLoading = true;
                tempInvoice = invoice;
                this.itemDetailsArray = invoice.invoiceItems;
                this.paymentModeList = invoice.paymentMode;
                this.invoiceId = invoice.invoiceId.toString();
                this.totalBillAmount = invoice.totalAmountAfterTax;
                this.paymentModeSetAmount = this.totalBillAmount;
                if(this.paymentModeList.length > 1) {
                    paymentMode = 'MULTI';
                    this.isMulti = true;
                } else {
                    this.paymentModeList.forEach((paymentModeData) => {
                        paymentMode = paymentModeData.paymentMode;
                    });
                }
                this.initFormControls(tempInvoice, paymentMode);
                this.isLoading = false;
            });
        } else {
            this.initFormControls(tempInvoice, paymentMode);
            this.getInvoiceNumber();
        }
    }

    private initFormControls(tempInvoice: Invoice, paymentMode: string) {
        this.invoiceForm = new FormGroup({
            customerName: new FormControl(tempInvoice.customerName, Validators.required),
            address: new FormControl(tempInvoice.address, Validators.maxLength(254)),
            state: new FormControl(tempInvoice.state, Validators.required),
            stateCode: new FormControl(tempInvoice.stateCode, Validators.required),
            gstin: new FormControl(tempInvoice.gstin),
            billDate: new FormControl(formatDate(tempInvoice.billDate, 'yyyy-MM-dd', 'en'), Validators.required),
            amountBeforeTax: new FormControl(tempInvoice.amountBeforeTax, Validators.required),
            paymentType: new FormControl(tempInvoice.paymentType, Validators.required),
            paymentMode: new FormControl(paymentMode, Validators.required),
            cgstAmount: new FormControl(tempInvoice.cgstAmount, Validators.required),
            sgstAmount: new FormControl(tempInvoice.sgstAmount, Validators.required),
            igstAmount: new FormControl(tempInvoice.igstAmount, Validators.required),
            invoiceType: new FormControl(tempInvoice.invoiceType, Validators.required),
            totalAmountAfterTax: new FormControl(tempInvoice.totalAmountAfterTax, Validators.required),
            phoneNumber: new FormControl(tempInvoice.phoneNumber),
            gstType: new FormControl(tempInvoice.gstType)
        });
    }

    private getTotalWeight(items: Array<Item>): number {
        let totalWeight = 0;
        items.forEach((item) => {
            totalWeight += item.grossWeight;
        });
        return totalWeight;
    }

    private setErrorDiv(message: string) {
        this.errorMessage = message;
        this.showErrorDiv = true;
        setTimeout(()=> {
            this.showErrorDiv = false;
        }, 4000);
    }

    private getPaymentModeDetails(): Array<PaymentMode> {
        if (this.isCredit) {
            return new Array<PaymentMode>();
        } else if (this.paymentModeList.length === 0) {
            this.paymentModeList.push({
                paymentMode: this.invoiceForm.controls.paymentMode.value,
                amount: this.totalBillAmount
            });
        }
        return this.paymentModeList;
    }
}
