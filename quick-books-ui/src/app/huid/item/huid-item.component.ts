import {Component, Input, OnInit} from '@angular/core';
import {HuidItem} from './huidItem';
import {ModalController} from '@ionic/angular';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {HuidService} from "../../services/huid.service";
import {formatDate} from "@angular/common";

@Component({
  selector: 'app-item',
  templateUrl: './huid-item.component.html',
  styleUrls: ['./huid-item.component.scss'],
})
export class HuidItemComponent implements OnInit {
  @Input() isEditMode = false;
  @Input() itemData: HuidItem;

  isInvalid = false;
  huidForm: FormGroup;

  constructor(private modalController: ModalController, private huidService: HuidService) {
  }

  ngOnInit() {
    this.initForm();
  }

  onCancel() {
    this.modalController.dismiss(null, 'cancel');
  }

  // autoInsertValues() {
  //   if (!!this.itemInvoiceForm.controls.itemWeight.value && !!this.itemInvoiceForm.controls.weightPerGram.value) {
  //     this.itemInvoiceForm.controls.amount.setValue(Math.round(this.itemInvoiceForm.controls.itemWeight.value *
  //       this.itemInvoiceForm.controls.weightPerGram.value));
  //   } else if (!!this.itemInvoiceForm.controls.itemWeight.value && !!!this.itemInvoiceForm.controls.weightPerGram.value
  //     && !!this.itemInvoiceForm.controls.amount.value) {
  //     let wpg = this.itemInvoiceForm.controls.amount.value / this.itemInvoiceForm.controls.itemWeight.value;
  //     wpg = +(Math.round(wpg * 100) / 100).toFixed(2);
  //     this.itemInvoiceForm.controls.weightPerGram.setValue(wpg);
  //   } else if (!!!this.itemInvoiceForm.controls.itemWeight.value && !!this.itemInvoiceForm.controls.weightPerGram.value
  //     && !!this.itemInvoiceForm.controls.amount.value) {
  //     let itemWeight = this.itemInvoiceForm.controls.amount.value / this.itemInvoiceForm.controls.weightPerGram.value;
  //     itemWeight = +(Math.round(itemWeight * 100) / 100).toFixed(3);
  //     itemWeight.toFixed(3);
  //     this.itemInvoiceForm.controls.itemWeight.setValue(itemWeight);
  //   }
  // }

  onSubmit() {
    if (this.huidForm.invalid) {
      this.isInvalid = true;
      return;
    }
      console.log("saving");

    let saled = false;
    let saledOn = undefined;
    if(this.isEditMode) {
        saled = this.itemData.saled;
        saledOn = this.itemData.saledOn;
    }
    const item: HuidItem = {
        huidNumber: this.huidForm.controls.huidNumber.value,
        itemName: this.huidForm.controls.itemName.value,
        createdOn: this.huidForm.controls.createdOn.value,
        grossWeight: this.huidForm.controls.grossWeight.value,
        saledOn,
        saled
    };
    this.huidService.persistHuid(item).subscribe(() => {
        this.modalController.dismiss({huidItem: item}, 'success');
    });

  }

  resetValues() {
    this.huidForm.controls.huidNumber.setValue('');
    this.huidForm.controls.itemName.setValue('');
    this.huidForm.controls.createdOn.setValue(new Date());
    this.huidForm.controls.grossWeight.setValue(0)
  }

  private initForm() {
    let huidNumber = undefined;
    let itemName = undefined;
    let createdOn = new Date();
    let grossWeight = 0;


    if(this.isEditMode === true) {
        huidNumber = this.itemData.huidNumber;
        itemName = this.itemData.itemName;
        createdOn = this.itemData.createdOn;
        grossWeight = this.itemData.grossWeight;
    }
      this.huidForm = new FormGroup({
          huidNumber: new FormControl(huidNumber, Validators.required),
          itemName: new FormControl(itemName, Validators.required),
          createdOn: new FormControl(formatDate(createdOn, 'yyyy-MM-dd', 'en'), Validators.required),
          grossWeight: new FormControl(grossWeight, Validators.required)
    });
  }
}
