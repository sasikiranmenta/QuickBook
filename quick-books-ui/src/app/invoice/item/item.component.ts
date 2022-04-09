import {Component, Input, OnInit} from '@angular/core';
import {Item} from './item';
import {ModalController} from '@ionic/angular';
import {FormControl, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-item',
  templateUrl: './item.component.html',
  styleUrls: ['./item.component.scss'],
})
export class ItemComponent implements OnInit {
  @Input() isEditMode = false;
  @Input() editItemData: Item;

  isInvalid = false;
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
      this.itemInvoiceForm.controls.amount.setValue(Math.round(this.itemInvoiceForm.controls.itemWeight.value *
        this.itemInvoiceForm.controls.weightPerGram.value));
    } else if (this.itemInvoiceForm.controls.itemWeight.value !== 0 &&
      this.itemInvoiceForm.controls.weightPerGram.value === 0
      && this.itemInvoiceForm.controls.amount.value !== 0) {
      this.itemInvoiceForm.controls.weightPerGram.setValue(Math.round(this.itemInvoiceForm.controls.amount.value *
        this.itemInvoiceForm.controls.itemWeight.value));
    } else if (this.itemInvoiceForm.controls.itemWeight.value === 0 &&
      this.itemInvoiceForm.controls.weightPerGram.value !== 0
      && this.itemInvoiceForm.controls.amount.value !== 0) {
      this.itemInvoiceForm.controls.itemWeight.setValue(Math.round(this.itemInvoiceForm.controls.amount.value *
        this.itemInvoiceForm.controls.weightPerGram.value));
    }
  }

  onSubmit() {
    if (this.itemInvoiceForm.invalid) {
      this.isInvalid = true;
      return;
    }
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
    let goodsDescription = '';
    let itemWeight;
    let ratePerGram;
    let totalAmount;

    if(this.isEditMode === true) {
      goodsDescription = this.editItemData.descriptionOfItem;
      itemWeight = this.editItemData.grossWeight;
      ratePerGram = this.editItemData.ratePerGram;
      totalAmount = this.editItemData.amount;
    }
      this.itemInvoiceForm = new FormGroup({
      goodsDescription: new FormControl(goodsDescription, Validators.required),
      itemWeight: new FormControl(itemWeight, Validators.required),
      weightPerGram: new FormControl(ratePerGram, Validators.required),
      amount: new FormControl(totalAmount)
    });
  }


}
