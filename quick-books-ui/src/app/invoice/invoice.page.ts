import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ModalController} from '@ionic/angular';
import {ItemComponent} from './item/item.component';
import {Item} from './item/item';
import {HttpService} from '../services/http.service';
import {CurrencyPipe, formatDate} from '@angular/common';
import {Invoice} from './invoice';
import {HttpClient, HttpResponse} from '@angular/common/http';

@Component({
  selector: 'app-invoice',
  templateUrl: './invoice.page.html',
  styleUrls: ['./invoice.page.scss'],
})
export class InvoicePage implements OnInit {
  invoiceForm: FormGroup;
  itemDetailsArray: Array<Item>;
  invoiceId = '';

  constructor(private modalController: ModalController, private httpService: HttpService, private currencyPipe: CurrencyPipe, private httpClient: HttpClient) {
  }

  ngOnInit() {
    this.itemDetailsArray = new Array<Item>();
    this.initForm();
  }


  onSubmit() {
    if (this.invoiceForm.invalid) {
      console.log('invalid');
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

  getInvoiceNumber() {
    this.httpService.get('http://localhost:8080/quick-book/getInvoiceNumber').subscribe((invoiceId) => {
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

    cgst = totalAmountBeforeTax * 1.5 / 100;
    sgst = totalAmountBeforeTax * 1.5 / 100;
    totalAmountAfterTax = totalAmountBeforeTax + cgst + sgst;
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
    invoice.invoiceType = 'GOLD';
    invoice.paymentType = 'CARD';
    invoice.amountBeforeTax = this.invoiceForm.controls.amountBeforeTax.value;
    invoice.cgstAmount = this.invoiceForm.controls.cgstAmount.value;
    invoice.sgstAmount = this.invoiceForm.controls.sgstAmount.value;
    invoice.igstAmount = this.invoiceForm.controls.igstAmount.value;
    invoice.totalAmountAfterTax = this.invoiceForm.controls.totalAmountAfterTax.value;
    this.httpClient.post('http://localhost:8080/quick-book/saveInvoice', invoice, {
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
      });
  }

  private initForm() {
    this.invoiceForm = new FormGroup({
      customerName: new FormControl(undefined, Validators.required),
      address: new FormControl(undefined, Validators.maxLength(254)),
      state: new FormControl(undefined, Validators.required),
      stateCode: new FormControl(undefined, Validators.required),
      gstin: new FormControl(undefined, Validators.required),
      billDate: new FormControl(formatDate(new Date(), 'yyyy-MM-dd', 'en'), Validators.required),
      amountBeforeTax: new FormControl(0, Validators.required),
      cgstAmount: new FormControl(0, Validators.required),
      sgstAmount: new FormControl(0, Validators.required),
      igstAmount: new FormControl(0, Validators.required),
      totalAmountAfterTax: new FormControl(0, Validators.required)
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
}
