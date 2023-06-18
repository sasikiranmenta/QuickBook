import {Component, OnInit} from '@angular/core';
import {ModalController} from '@ionic/angular';
import {UntypedFormControl, UntypedFormGroup, Validators} from '@angular/forms';

@Component({
    selector: 'app-item',
    templateUrl: './add-model.component.html',
    styleUrls: ['./add-model.component.scss'],
})
export class AddModelComponent implements OnInit {

    modelForm: UntypedFormGroup;

    constructor(private modalController: ModalController) {
    }

    ngOnInit() {
        this.initForm();
    }

    onCancel() {
        this.modalController.dismiss(null, 'cancel');
    }

    onSubmit() {
        this.modalController.dismiss({model: this.modelForm.controls.modelName.value}, 'success');
    }

    private initForm() {
        this.modelForm = new UntypedFormGroup({
            modelName: new UntypedFormControl('', Validators.required),
        });
    }
}
