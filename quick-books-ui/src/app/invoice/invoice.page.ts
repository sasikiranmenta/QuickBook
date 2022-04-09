import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ModalController} from '@ionic/angular';
import {ItemComponent} from './item/item.component';
import {Item} from './item/item';
import {HttpService} from '../services/http.service';
import {formatDate} from '@angular/common';
import {Invoice} from './invoice';

@Component({
  selector: 'app-invoice',
  templateUrl: './invoice.page.html',
  styleUrls: ['./invoice.page.scss'],
})
export class InvoicePage implements OnInit {
  invoiceForm: FormGroup;
  itemDetailsArray: Array<Item>;
  invoiceId = '';
  isInvalid = false;

  constructor(private modalController: ModalController,
              private httpService: HttpService) {
  }

  ngOnInit() {
    this.itemDetailsArray = new Array<Item>();
    this.initForm();
    this.isInvalid = false;
  }


  onSubmit() {
    if (this.invoiceForm.invalid || this.itemDetailsArray.length === 0) {
      this.isInvalid = true;
      return;
    }
    this.saveIntoDb();
  }

  onAddItem() {
    console.log('hi');
    this.modalController.create({component: ItemComponent, componentProps: {selectedMode: 'add'}})
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
    this.modalController.create({component: ItemComponent, componentProps: {isEditMode: true, editItemData: this.itemDetailsArray[i]}})
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
    this.httpService.get('/quick-book/getInvoiceNumber').subscribe((invoiceId) => {
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

  private saveIntoDb() {
    const invoice: Invoice = this.invoiceForm.value;
    invoice.invoiceItems = this.itemDetailsArray;
    invoice.amountBeforeTax = this.invoiceForm.controls.amountBeforeTax.value;
    invoice.cgstAmount = this.invoiceForm.controls.cgstAmount.value;
    invoice.sgstAmount = this.invoiceForm.controls.sgstAmount.value;
    invoice.igstAmount = this.invoiceForm.controls.igstAmount.value;
    invoice.totalAmountAfterTax = this.invoiceForm.controls.totalAmountAfterTax.value;
    invoice.totalWeight = this.getTotalWeight(invoice.invoiceItems);
    this.httpService.post('/quick-book/saveInvoice', invoice, {
      observe: 'response',
      responseType: 'blob'
    })
      .subscribe((response) => {
        const file = new Blob([response.body], {type: 'application/pdf'});
        const fileURL = URL.createObjectURL(file);
// if you want to open PDF in new tab
        window.open(fileURL);
        const a = document.createElement('a');
        a.href = fileURL;
        a.target = '_blank';
        a.download = response.headers.get('file_name');
        document.body.appendChild(a);
        a.click();
        this.ngOnInit();
      });
  }

  private initForm() {
    this.invoiceForm = new FormGroup({
      customerName: new FormControl(undefined, Validators.required),
      address: new FormControl(undefined, Validators.maxLength(254)),
      state: new FormControl(undefined, Validators.required),
      stateCode: new FormControl(undefined, Validators.required),
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
      phoneNumber: new FormControl(undefined, )
    });
    this.getInvoiceNumber();


    // private QuickBookHSNEnum invoiceType;
    // private PaymentTypeEnum paymentType;
    // private List<InvoiceItem> invoiceItems;

    // this.invoiceForm.valueChanges
    // .pipe(takeUntil()) TODO
    // .subscribe(form => {
    // const afterTax: string =  this.invoiceForm.controls.afterTax.value;
    // console.log('afterTax', afterTax);
    // console.log(this.currencyPipe.transform(afterTax, 'INR', 'symbol', '1.0-0', 'en_IN'));
    // this.invoiceForm.controls.afterTax.setValue(
    //   this.currencyPipe.transform(afterTax.toString().replace(/\D/g, '').
    //   replace(/^0+/,''), 'INR', 'symbol')
    // );
    // });
  }

  private getTotalWeight(items: Array<Item>): number {
    let totalWeight = 0;
    items.forEach((item) => {
      totalWeight += item.grossWeight;
    });
    return totalWeight;
  }





}
