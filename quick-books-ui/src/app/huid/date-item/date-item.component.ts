import {Component, OnInit} from '@angular/core';
import {ModalController} from '@ionic/angular';
import {UntypedFormControl, UntypedFormGroup, Validators} from '@angular/forms';
import {formatDate} from "@angular/common";

@Component({
    selector: 'app-item',
    templateUrl: './date-item.component.html',
    styleUrls: ['./date-item.component.scss'],
})
export class DateItemComponent implements OnInit {

    date = new Date();

    dateForm: UntypedFormGroup;

    constructor(private modalController: ModalController) {
    }

    ngOnInit() {
        this.initForm();
    }

    onCancel() {
        this.modalController.dismiss(null, 'cancel');
    }

    onSubmit() {
        console.log("to be saled");
        this.modalController.dismiss({saledOn: this.dateForm.controls.saledOn.value}, 'success');
    }


    private initForm() {
        this.dateForm = new UntypedFormGroup({
            saledOn: new UntypedFormControl(formatDate(this.date, 'yyyy-MM-dd', 'en'), Validators.required),
        });
    }
}
