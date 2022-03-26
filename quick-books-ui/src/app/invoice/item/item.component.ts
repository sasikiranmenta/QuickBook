import {Component, OnInit} from '@angular/core';
import {Item} from './item';
import {ModalController} from '@ionic/angular';
import {FormControl, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-item',
  templateUrl: './item.component.html',
  styleUrls: ['./item.component.scss'],
})
export class ItemComponent implements OnInit {

  itemInvoiceForm: FormGroup;

  constructor(private modalController: ModalController) {
  }


  ngOnInit() {
    this.initForm();
  }

  onCancel() {
    this.modalController.dismiss(null, 'cancel');
  }

  autoInsertValues() {
    if (this.itemInvoiceForm.controls.itemWeight.value !== 0 &&
      this.itemInvoiceForm.controls.weightPerGram.value !== 0) {
      this.itemInvoiceForm.controls.amount.setValue(this.itemInvoiceForm.controls.itemWeight.value *
        this.itemInvoiceForm.controls.weightPerGram.value);
    } else if (this.itemInvoiceForm.controls.itemWeight.value !== 0 &&
      this.itemInvoiceForm.controls.weightPerGram.value === 0
      && this.itemInvoiceForm.controls.amount.value !== 0) {
      this.itemInvoiceForm.controls.weightPerGram.setValue(this.itemInvoiceForm.controls.amount.value *
        this.itemInvoiceForm.controls.itemWeight.value);
    } else if (this.itemInvoiceForm.controls.itemWeight.value === 0 &&
      this.itemInvoiceForm.controls.weightPerGram.value !== 0
      && this.itemInvoiceForm.controls.amount.value !== 0) {
      this.itemInvoiceForm.controls.itemWeight.setValue(this.itemInvoiceForm.controls.amount.value *
        this.itemInvoiceForm.controls.weightPerGram.value);
    }
  }

  onSubmit() {
    this.autoInsertValues();
    const newItem: Item = {
      amount: this.itemInvoiceForm.controls.amount.value,
      descriptionOfItem: this.itemInvoiceForm.controls.goodsDescription.value,
      grossWeight: this.itemInvoiceForm.controls.itemWeight.value,
      ratePerGram: this.itemInvoiceForm.controls.weightPerGram.value
    };
    console.log(this.itemInvoiceForm.controls.itemWeight.value, this.itemInvoiceForm.controls.weightPerGram.value);
    this.modalController.dismiss({invoiceItem: newItem}, 'confirm');

  }

  private initForm() {
    this.itemInvoiceForm = new FormGroup({
      goodsDescription: new FormControl('', Validators.required),
      itemWeight: new FormControl(undefined),
      weightPerGram: new FormControl(undefined),
      amount: new FormControl(0)
    });
  }


}
