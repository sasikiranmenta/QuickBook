import {Component} from '@angular/core';

import {ICellRendererAngularComp} from "ag-grid-angular";
import {ICellRendererParams} from "ag-grid-community";
import {AlertController, ModalController} from "@ionic/angular";
import {DateItemComponent} from "../date-item/date-item.component";
import {HuidItem} from "../item/huidItem";
import {ConfirmItemComponent} from "../confirm-item/confirm-item.component";

@Component({
    selector: 'app-item',
    templateUrl: './sale-cell-renderer.component.html',
    styleUrls: ['./sale-cell-renderer.component.scss'],
})
export class SaleCellRendererComponent implements ICellRendererAngularComp {
    public itemData: HuidItem;
    public saleChangeCallback: (item: HuidItem) => {};

    constructor(private modalController: ModalController, private alertController: AlertController) {
    }

    // gets called once before the renderer is used
    agInit(params: ICellRendererParams): void {
        console.log(params);
        this.refresh(params);
    }

    // gets called whenever the cell refreshes
    refresh(params: any): boolean {
        // set value into cell again
        this.itemData = params.data;
        this.saleChangeCallback = params.saleChangeCallback;
        return true;
    }

    onSaleClicked() {
        if (!this.itemData.saled) {
            this.modalController.create({
                component: DateItemComponent
            }).then((modalElement) => {
                modalElement.present();
                return modalElement.onDidDismiss();
            }).then((saledOn) => {
                if (saledOn.role === 'success') {
                    this.itemData.saled = true;
                    this.itemData.saledOn = saledOn.data.saledOn;
                    this.saleChangeCallback(this.itemData)
                }
            });
        } else {
            this.moveBackToStockAlert();
        }
    }

    moveBackToStockAlert() {
        this.alertController.create({
            header: 'Are you sure to move item back to stock?',
            buttons: [
                {
                    text: 'Yes',
                    role: 'success',
                    cssClass: 'secondary',
                    handler: () => {
                        this.alertController.dismiss(null, 'success');
                    }
                },
                {
                    text: 'No',
                    role: 'cancel',
                    cssClass: 'secondary',
                    handler: () => {
                        this.alertController.dismiss(null, 'cancel');
                    }
                }
            ]
        }).then((presentEl) => {
            presentEl.present();
            return presentEl.onDidDismiss();
        }).then((dismisEl) => {
            if (dismisEl.role === 'success') {
                this.itemData.saled = false;
                this.itemData.saledOn = undefined;
                this.saleChangeCallback(this.itemData)
            }
        });
    }

}
