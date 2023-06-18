import {NgModule} from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {IonicModule} from '@ionic/angular';

import {HuidPageRoutingModule} from './huid-routing.module';

import {HuidPage} from './huid-page.component';
import {AgGridModule} from 'ag-grid-angular';
import {HuidItemComponent} from "./item/huid-item.component";
import {SaleCellRendererComponent} from "./sale-cell-renderer/sale-cell-renderer.component";
import {DateItemComponent} from "./date-item/date-item.component";
import {ConfirmItemComponent} from "./confirm-item/confirm-item.component";
import {NgSelectModule} from "@ng-select/ng-select";
import {AddModelComponent} from "./add-model/add-model.component";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        IonicModule,
        HuidPageRoutingModule,
        AgGridModule,
        ReactiveFormsModule,
        NgSelectModule
    ],
    declarations: [HuidPage, AddModelComponent, ConfirmItemComponent, HuidItemComponent, SaleCellRendererComponent,DateItemComponent],
    exports: [],
    providers: [DatePipe]
})
export class HuidPageModule {
}
