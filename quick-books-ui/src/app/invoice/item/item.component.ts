import {Component, Input, OnInit} from '@angular/core';
import {Item} from './item';
import {ModalController} from '@ionic/angular';
import {UntypedFormControl, UntypedFormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-item',
  templateUrl: './item.component.html',
  styleUrls: ['./item.component.scss'],
})
export class ItemComponent implements OnInit {
  @Input() isEditMode = false;
  @Input() editItemData: Item;

  isInvalid = false;
  itemInvoiceForm: UntypedFormGroup;

  constructor(private modalController: ModalController) {
  }


  ngOnInit() {
    this.initForm();
  }

  onCancel() {
    this.modalController.dismiss(null, 'cancel');
  }

  autoInsertValues() {
    if (!!this.itemInvoiceForm.controls.itemWeight.value && !!this.itemInvoiceForm.controls.weightPerGram.value) {
      this.itemInvoiceForm.controls.amount.setValue(Math.round(this.itemInvoiceForm.controls.itemWeight.value *
        this.itemInvoiceForm.controls.weightPerGram.value));
    } else if (!!this.itemInvoiceForm.controls.itemWeight.value && !!!this.itemInvoiceForm.controls.weightPerGram.value
      && !!this.itemInvoiceForm.controls.amount.value) {
      let wpg = this.itemInvoiceForm.controls.amount.value / this.itemInvoiceForm.controls.itemWeight.value;
      wpg = +(Math.round(wpg * 100) / 100).toFixed(2);
      this.itemInvoiceForm.controls.weightPerGram.setValue(wpg);
    } else if (!!!this.itemInvoiceForm.controls.itemWeight.value && !!this.itemInvoiceForm.controls.weightPerGram.value
      && !!this.itemInvoiceForm.controls.amount.value) {
      let itemWeight = this.itemInvoiceForm.controls.amount.value / this.itemInvoiceForm.controls.weightPerGram.value;
      itemWeight = +(Math.round(itemWeight * 100) / 100).toFixed(3);
      itemWeight.toFixed(3);
      this.itemInvoiceForm.controls.itemWeight.setValue(itemWeight);
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
    this.modalController.dismiss({invoiceItem: newItem}, 'confirm');

  }

  resetValues() {
    this.itemInvoiceForm.controls.amount.setValue(undefined);
    this.itemInvoiceForm.controls.weightPerGram.setValue(undefined);
    this.itemInvoiceForm.controls.itemWeight.setValue(undefined);
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
      this.itemInvoiceForm = new UntypedFormGroup({
      goodsDescription: new UntypedFormControl(goodsDescription, Validators.required),
      itemWeight: new UntypedFormControl(itemWeight, Validators.required),
      weightPerGram: new UntypedFormControl(ratePerGram, Validators.required),
      amount: new UntypedFormControl(totalAmount)
    });
  }
}
