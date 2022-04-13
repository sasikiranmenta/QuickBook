import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { InvoicePageRoutingModule } from './invoice-routing.module';

import { InvoicePage } from './invoice.page';
import {ItemComponent} from './item/item.component';
import {PaymentModeComponent} from './payment-mode/payment-mode.component';
import {ViewInvoicePageModule} from '../view-invoice/view-invoice.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        IonicModule,
        InvoicePageRoutingModule,
        ViewInvoicePageModule
    ],
  declarations: [InvoicePage, ItemComponent, PaymentModeComponent]
})
export class InvoicePageModule {}
