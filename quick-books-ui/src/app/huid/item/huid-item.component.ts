import {Component, Input, OnInit} from '@angular/core';
import {HuidItem} from './huidItem';
import {ModalController} from '@ionic/angular';
import {UntypedFormControl, UntypedFormGroup, Validators} from '@angular/forms';
import {HuidService} from "../../services/huid.service";
import {formatDate} from "@angular/common";
import {ItemModelsService} from "../../services/items-models.service";
import {ItemModels} from "./itemmodel";
import {AddModelComponent} from "../add-model/add-model.component";

@Component({
    selector: 'app-item',
    templateUrl: './huid-item.component.html',
    styleUrls: ['./huid-item.component.scss'],
})
export class HuidItemComponent implements OnInit {
    @Input() isEditMode = false;
    @Input() itemData: HuidItem;

    isInvalid = false;
    huidForm: UntypedFormGroup;
    itemModel: ItemModels;
    isLoading: boolean = false;

    constructor(private modalController: ModalController, private huidService: HuidService, private itemModelsService: ItemModelsService) {
    }

    ngOnInit() {
        this.isLoading = true;
        this.itemModelsService.getItemModelsByItemType('GOLD').subscribe(itemModels => {
            if (!!itemModels) {
                this.itemModel = itemModels
            } else {
                this.itemModel = {models: new Array<string>(), itemType: 'GOLD'};
            }
            this.initForm();
            this.isLoading = false;
        });

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
        if (this.isEditMode) {
            saled = this.itemData.saled;
            saledOn = this.itemData.saledOn;
        }
        const item: HuidItem = {
            huidNumber: this.huidForm.controls.huidNumber.value,
            itemName: this.huidForm.controls.itemName.value,
            createdOn: this.huidForm.controls.createdOn.value,
            grossWeight: this.huidForm.controls.grossWeight.value,
            itemType: this.huidForm.controls.itemType.value,
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

    addNewItemModel(itemModeName: string) {
        this.itemModel.models.push(itemModeName.toLowerCase());
        this.itemModelsService.persistItemModel(this.itemModel).subscribe(() => this.getItemModels(this.huidForm.controls.itemType.value));
    }

    getItemModels(itemType: 'GOLD' | 'SILVER') {
        this.itemModelsService.getItemModelsByItemType(itemType).subscribe(itemModels => {
            if (!!itemModels) {
                this.itemModel = itemModels
            } else {
                this.itemModel = {models: new Array<string>(), itemType: this.huidForm.controls.itemType.value};
            }
        });
    }

    onItemTypeChanged() {
        this.getItemModels(this.huidForm.controls.itemType.value);
    }

    onAddNewModel() {
        this.modalController.create({
            component: AddModelComponent,
        }).then((modalElement) => {
            modalElement.present();
            return modalElement.onDidDismiss();
        }).then((modelName) => {
            if (modelName.role === 'success') {
                this.addNewItemModel(modelName.data.model);
            }
        });
    }

    private initForm() {
        let huidNumber = undefined;
        let itemName = undefined;
        let createdOn = new Date();
        let grossWeight = 0;
        let itemType = 'GOLD';


        if (this.isEditMode === true) {
            huidNumber = this.itemData.huidNumber;
            itemName = this.itemData.itemName;
            createdOn = this.itemData.createdOn;
            grossWeight = this.itemData.grossWeight;
            itemType = this.itemData.itemType;
        }
        this.huidForm = new UntypedFormGroup({
            huidNumber: new UntypedFormControl(huidNumber, Validators.required),
            itemName: new UntypedFormControl(itemName, Validators.required),
            createdOn: new UntypedFormControl(formatDate(createdOn, 'yyyy-MM-dd', 'en'), Validators.required),
            grossWeight: new UntypedFormControl(grossWeight, Validators.required),
            itemType: new UntypedFormControl(itemType, Validators.required)
        });

    }
}
