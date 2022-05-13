import {Component, OnInit} from '@angular/core';
import {PopoverController} from '@ionic/angular';
import {FormControl, FormGroup, Validators} from '@angular/forms';

@Component({
    selector: 'app-email',
    templateUrl: './email.component.html',
    styleUrls: ['./email.component.scss'],
})
export class EmailComponent implements OnInit {

    emailForm: FormGroup;
    constructor(private popoverController: PopoverController) {
    }

    ngOnInit() {
        this.emailForm = new FormGroup({
            emailId: new FormControl('', Validators.email)});
    }

    onSendMail() {
        if (this.emailForm.invalid) {
            return;
        }
        this.popoverController.dismiss(this.emailForm.controls.emailId.value, 'success');
    }

    onCancel() {
        this.popoverController.dismiss(null,'cancel');
    }
}
