import {Component, OnInit} from '@angular/core';
import {ModalController} from '@ionic/angular';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {formatDate} from "@angular/common";

@Component({
    selector: 'app-item',
    templateUrl: './confirm-item.component.html',
    styleUrls: ['./confirm-item.component.scss'],
})
export class ConfirmItemComponent {

    constructor(private modalController: ModalController) {
    }

    onCancel() {
        this.modalController.dismiss(null, 'cancel');
    }

    onSubmit() {
        this.modalController.dismiss(null, 'success');
    }

}
