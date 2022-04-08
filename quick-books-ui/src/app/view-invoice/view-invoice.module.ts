import { NgModule } from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { ViewInvoicePageRoutingModule } from './view-invoice-routing.module';

import { ViewInvoicePage } from './view-invoice.page';
import {AgGridModule} from 'ag-grid-angular';
import {IndianCurrency} from '../pipe/indian-currency.pipe';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    ViewInvoicePageRoutingModule,
    AgGridModule,
    ReactiveFormsModule
  ],
  declarations: [ViewInvoicePage, IndianCurrency],
  providers: [IndianCurrency, DatePipe]
})
export class ViewInvoicePageModule {}
