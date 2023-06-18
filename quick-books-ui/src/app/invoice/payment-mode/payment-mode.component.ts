import {Component, Input, OnInit} from '@angular/core';
import {UntypedFormControl, UntypedFormGroup} from '@angular/forms';
import {ModalController} from '@ionic/angular';
import {getNewPaymentDetailStateMap, PaymentModeDetail} from './payment-mode';
import {PaymentMode} from '../invoice';

@Component({
    selector: 'app-payment-mode',
    templateUrl: './payment-mode.component.html',
    styleUrls: ['./payment-mode.component.scss'],
})
export class PaymentModeComponent implements OnInit {

    @Input() paymentModeList: Array<PaymentMode> = new Array<PaymentMode>();
    @Input() totalBillAmount = 10000;

    paymentModeStateMap: Map<string, PaymentModeDetail> = new Map<string, PaymentModeDetail>();
    showError = false;
    remainingAmount = 0;
    paymentModeGroup: UntypedFormGroup;

    constructor(private modalController: ModalController) {
    }

    ngOnInit() {
        this.setPaymentModeDetailList();
        this.initForm();
        this.calculateRemainingAmount();
    }

    calculateRemainingAmount() {
        let tempTotal = 0;
        Object.keys(this.paymentModeGroup.controls).forEach(key => {
            const amount = this.paymentModeGroup.get(key).value;
            if (!!amount) {
                tempTotal += amount;
            }
        });
        this.remainingAmount = this.totalBillAmount - tempTotal;
    }

    onCloseModal() {
        this.modalController.dismiss(undefined, 'cancel');
    }

    onCheckboxToggled(paymentModeDetail: PaymentModeDetail) {
        this.paymentModeGroup.controls[paymentModeDetail.name].setValue(undefined);
        paymentModeDetail.isChecked = !paymentModeDetail.isChecked;
        this.calculateRemainingAmount();
    }

    onReset() {
        this.paymentModeStateMap = getNewPaymentDetailStateMap();
        this.initForm();
        this.calculateRemainingAmount();
    }

    onSave() {
        if (this.remainingAmount !== 0) {
            this.showErrorDiv();
            return;
        }
        this.modalController.dismiss({list: this.getPaymentModesList(), paymentAmount: this.totalBillAmount}, 'success');
    }

    private initForm() {
        this.paymentModeGroup = new UntypedFormGroup({});
        this.paymentModeStateMap.forEach((paymentModeDetailValue) => {
            this.paymentModeGroup.addControl(
                paymentModeDetailValue.name,
                new UntypedFormControl(paymentModeDetailValue.amount === 0 ? undefined : paymentModeDetailValue.amount));
        });
    }

    private showErrorDiv() {
        this.showError = true;
        setTimeout(() => {
            this.showError = false;
        }, 4000);
    }

    private getPaymentModesList(): Array<PaymentMode> {
        const paymentModeList: Array<PaymentMode> = [];
        Object.keys(this.paymentModeGroup.controls).forEach((key) => {
            const amount = this.paymentModeGroup.get(key).value;
            if (!!amount) {
                paymentModeList.push({amount, paymentMode: key});
            }
        });
        return paymentModeList;
    }

    private setPaymentModeDetailList() {
        this.paymentModeStateMap = getNewPaymentDetailStateMap();
        this.paymentModeList.forEach((paymentMode) => {
            this.paymentModeStateMap.get(paymentMode.paymentMode).amount = paymentMode.amount;
            this.paymentModeStateMap.get(paymentMode.paymentMode).isChecked = true;
        });
    }
}
