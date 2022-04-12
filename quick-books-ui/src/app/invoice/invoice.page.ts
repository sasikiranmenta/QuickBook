import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ModalController} from '@ionic/angular';
import {ItemComponent} from './item/item.component';
import {Item} from './item/item';
import {formatDate} from '@angular/common';
import {Invoice} from './invoice';
import {InvoiceService} from '../services/invoice.service';

@Component({
    selector: 'app-invoice', templateUrl: './invoice.page.html', styleUrls: ['./invoice.page.scss'],
})
export class InvoicePage implements OnInit {
    invoiceForm: FormGroup;
    itemDetailsArray: Array<Item>;
    invoiceId = '';
    isInvalid = false;

    constructor(private modalController: ModalController, private invoiceService: InvoiceService) {
    }

    ngOnInit() {
        this.itemDetailsArray = new Array<Item>();
        this.initForm();
        this.isInvalid = false;
    }

    onSubmit(print = true) {
        if (this.invoiceForm.invalid || this.itemDetailsArray.length === 0) {
            this.isInvalid = true;
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
        this.invoiceForm.controls.amountBeforeTax.setValue(totalAmountBeforeTax);
        this.invoiceForm.controls.cgstAmount.setValue(cgst);
        this.invoiceForm.controls.sgstAmount.setValue(sgst);
        this.invoiceForm.controls.totalAmountAfterTax.setValue(totalAmountAfterTax);
    }

    onCancel() {

    }

    private saveIntoDb(isPrint: boolean) {
        const invoice: Invoice = this.invoiceForm.value;
        invoice.invoiceItems = this.itemDetailsArray;
        invoice.amountBeforeTax = this.invoiceForm.controls.amountBeforeTax.value;
        invoice.cgstAmount = this.invoiceForm.controls.cgstAmount.value;
        invoice.sgstAmount = this.invoiceForm.controls.sgstAmount.value;
        invoice.igstAmount = this.invoiceForm.controls.igstAmount.value;
        invoice.totalAmountAfterTax = this.invoiceForm.controls.totalAmountAfterTax.value;
        invoice.totalWeight = this.getTotalWeight(invoice.invoiceItems);

        this.invoiceService.saveInvoice(invoice, isPrint)
            .subscribe((response) => {
                if (isPrint) {
                    this.invoiceService.downloadPDF(response);
                }
                this.ngOnInit();
            });
    }

    private initForm() {
        this.invoiceForm = new FormGroup({
            customerName: new FormControl(undefined, Validators.required),
            address: new FormControl('Nellore', Validators.maxLength(254)),
            state: new FormControl('AP', Validators.required),
            stateCode: new FormControl(37, Validators.required),
            gstin: new FormControl(undefined),
            billDate: new FormControl(formatDate(new Date(), 'yyyy-MM-dd', 'en'), Validators.required),
            amountBeforeTax: new FormControl(0, Validators.required),
            paymentType: new FormControl('CASH', Validators.required),
            paymentMode: new FormControl('CASH', Validators.required),
            cgstAmount: new FormControl(0, Validators.required),
            sgstAmount: new FormControl(0, Validators.required),
            igstAmount: new FormControl(0, Validators.required),
            invoiceType: new FormControl('GOLD', Validators.required),
            totalAmountAfterTax: new FormControl(0, Validators.required),
            phoneNumber: new FormControl(undefined,)
        });
        this.getInvoiceNumber();
    }

    private getTotalWeight(items: Array<Item>): number {
        let totalWeight = 0;
        items.forEach((item) => {
            totalWeight += item.grossWeight;
        });
        return totalWeight;
    }
}
