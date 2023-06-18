import {Component, OnInit, ViewChild} from '@angular/core';
import {
    ColDef,
    FilterChangedEvent,
    GridReadyEvent,
    RowDataChangedEvent,
    RowDoubleClickedEvent
} from 'ag-grid-community';
import {AgGridAngular} from 'ag-grid-angular';
import {DatePipe, formatDate} from '@angular/common';
import {UntypedFormControl, UntypedFormGroup, Validators} from '@angular/forms';
import {TotalValue} from '../invoice/invoice';
import {AlertController, ModalController, PopoverController} from '@ionic/angular';
import {HuidItemComponent} from "./item/huid-item.component";
import {HuidService} from "../services/huid.service";
import {HuidItem} from "./item/huidItem";
import {SaleCellRendererComponent} from "./sale-cell-renderer/sale-cell-renderer.component";
import {HuidRequestBody} from "./item/huid-request-body";
import {HuidSummary} from "./item/huidResponse";
import {EmailComponent} from "../view-invoice/email/email.component";

@Component({
    selector: 'app-view-invoice', templateUrl: './huid-page.component.html', styleUrls: ['./huid-page.component.scss'],
})
export class HuidPage implements OnInit {

    @ViewChild('agGrid') agGrid: AgGridAngular;


    includeSaledData = true;
    includeInstockData = true;
    showMessage = false;
    messageClass = '';
    errorMessage = '';
    emailLoading = false;
    dateRangeFilter = 'saled';
    template = '<span class="ag-overlay-loading-center">Please wait while data is loading</span>';
    pageSummary: HuidSummary;
    summaryLoading: boolean = false;

    dateGroup: UntypedFormGroup;

    silverDetails: TotalValue = {
        totalAfterTax: 0, totalBeforeTax: 0, totalCgst: 0, totalIgst: 0, totalSgst: 0, totalWeight: 0
    };
    goldDetails: TotalValue = {
        totalAfterTax: 0, totalBeforeTax: 0, totalCgst: 0, totalIgst: 0, totalSgst: 0, totalWeight: 0
    };


    public columnDefs: ColDef[] = [
        { headerName: 'HUID Created Date', field: 'createdOn', filter: 'agDateColumnFilter', valueFormatter: (param) => this.datePipe.transform(param.value, 'dd/MM/YYYY') },
        { headerName: 'HUID NO.', field: 'huidNumber', filter: 'agSetColumnFilter' },
        { headerName: 'Item name', field: 'itemName', filter: 'agSetColumnFilter' },
        { headerName: 'Weight', field: 'grossWeight', filter: 'agSetColumnFilter' },
        { headerName: 'Status (stock / saled)', field: 'isSaled', filter: 'agSetColumnFilter', cellRenderer: SaleCellRendererComponent, cellRendererParams: {saleChangeCallback: this.onSaleChanged.bind(this)} },
        { headerName: 'Sale Date', field: 'saledOn', filter: 'agDateColumnFilter',  valueFormatter: (param) => this.datePipe.transform(param.value, 'dd/MM/YYYY') }
    ];

    rowData: Array<HuidItem>;
    defaultColDef: ColDef = {
        flex: 1, resizable: true
    };


    constructor(private huidService: HuidService,
                private datePipe: DatePipe,
                private alertController: AlertController,
                private modalController: ModalController, private popoverController: PopoverController) {
    }

    ngOnInit() {
        this.summaryLoading = true;
        this.intializeTotalsToZero();
        const currentDate = new Date();
        const financialStartDate = this.getFinancialStartDate(currentDate);

        this.initForm(financialStartDate, currentDate);
        this.getHuidData();
    }

    onGridReady(params: GridReadyEvent) {
    }

    onRowEdit($event: RowDoubleClickedEvent) {
        console.log($event);
        let data = $event.data;
        this.alertController.create({
            header: 'Click one from below',
            buttons: [
                {
                    text: 'Edit',
                    role: 'edit',
                    cssClass: 'secondary',
                    handler: () => {
                        this.alertController.dismiss(null, 'edit');
                    }
                },
                {
                    text: 'Cancel',
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
            if(dismisEl.role == 'edit') {
                this.showModal(data);
            }
        });
    }

    onRowDataChanged($event: RowDataChangedEvent) {
        this.setTotal();
    }

    getHuidData() {
        this.summaryLoading = true;
        let requestBody: HuidRequestBody = {
            from:  new Date(this.dateGroup.value.fromDate),
            to: new Date(this.dateGroup.value.toDate),
            applyDateRangeOn: this.dateGroup.value.dateRangeOn,
            includeSaledData: this.includeSaledData,
            includeStockData: this.includeInstockData
        }
        this.huidService.getAllHuid(requestBody).subscribe((response) => {
            this.rowData = response.data;
            this.pageSummary = response.summary;
            this.summaryLoading = false;
        });
    }

    getFinancialStartDate(currentDate: Date) {
        let previousYear: number = currentDate.getFullYear();
        if (currentDate.getUTCMonth() < 3) {
            previousYear = previousYear - 1;
        }
        return new Date(previousYear, 3, 1);
    }

    onFilterChange($event: FilterChangedEvent) {
        this.setTotal();
    }

    setTotal() {
        this.intializeTotalsToZero();
        this.agGrid.api.forEachNodeAfterFilter((rowNode, index) => {
            if (rowNode.data.invoiceType === 'GOLD') {
                this.goldDetails.totalBeforeTax += rowNode.data.amountBeforeTax;
                this.goldDetails.totalCgst += rowNode.data.cgstAmount;
                this.goldDetails.totalSgst += rowNode.data.sgstAmount;
                this.goldDetails.totalAfterTax += rowNode.data.totalAmountAfterTax;
                this.goldDetails.totalWeight += rowNode.data.totalWeight;
            } else if (rowNode.data.invoiceType === 'SILVER') {
                this.silverDetails.totalBeforeTax += rowNode.data.amountBeforeTax;
                this.silverDetails.totalCgst += rowNode.data.cgstAmount;
                this.silverDetails.totalSgst += rowNode.data.sgstAmount;
                this.silverDetails.totalAfterTax += rowNode.data.totalAmountAfterTax;
                this.silverDetails.totalWeight += rowNode.data.totalWeight;
            }
        });
    }

    onSaleChanged(item: HuidItem) {
        this.huidService.persistHuid(item).subscribe(() => this.getHuidData());
    }

    intializeTotalsToZero() {
        this.goldDetails.totalBeforeTax = 0;
        this.goldDetails.totalCgst = 0;
        this.goldDetails.totalSgst = 0;
        this.goldDetails.totalIgst = 0;
        this.goldDetails.totalAfterTax = 0;
        this.goldDetails.totalWeight = 0;

        this.silverDetails.totalBeforeTax = 0;
        this.silverDetails.totalCgst = 0;
        this.silverDetails.totalSgst = 0;
        this.silverDetails.totalIgst = 0;
        this.silverDetails.totalAfterTax = 0;
        this.silverDetails.totalWeight = 0;

    }

    initForm(financialStartDate: Date, currentDate: Date) {
        this.dateGroup = new UntypedFormGroup({
            fromDate: new UntypedFormControl(formatDate(financialStartDate, 'yyyy-MM-dd', 'en'), Validators.required),
            toDate: new UntypedFormControl(formatDate(currentDate, 'yyyy-MM-dd', 'en'), Validators.required),
            dateRangeOn: new UntypedFormControl('SALED')
        });
    }

    onSaleCheckBoxChange() {
        if(!this.includeInstockData && !this.includeSaledData) {
            this.includeInstockData = true;
        }
        this.getHuidData();
    }

    onStockCheckBoxChange() {
        if(!this.includeInstockData && !this.includeSaledData) {
            this.includeSaledData = true;
        }
        this.getHuidData();
    }


    sendEmail(event) {

        let requestBody: HuidRequestBody = {
            from:  new Date(this.dateGroup.value.fromDate),
            to: new Date(this.dateGroup.value.toDate),
            applyDateRangeOn: this.dateGroup.value.dateRangeOn,
            includeSaledData: this.includeSaledData,
            includeStockData: this.includeInstockData
        };

        this.popoverController.create({
            component: EmailComponent,
            event,
            showBackdrop: false
        }).then((el) => {
            el.present();
            return el.onDidDismiss();
        }).then((data) => {
            if (data.role === 'success') {
                this.emailLoading = true;
                this.huidService.sendEmail(requestBody, data.data)
                    .subscribe(() => {
                        this.emailLoading = false;
                        this.setMessage(`Mail sent to ${data.data} successfully`, 'success');
                    }, () => {
                        this.emailLoading = false;
                        this.setMessage(`Error sending mail. Check internet connection`, 'error');
                    });
            }
        });
    }

    showModal(data?: HuidItem) {
        this.modalController.create({
            component: HuidItemComponent,
            componentProps: {
                isEditMode: !!data,
                itemData: data
            }
        }).then((modalElement) => {
            modalElement.present();
            return modalElement.onDidDismiss();
        }).then((huidModalResponse) => {
            if (huidModalResponse.role === 'success') {
                this.getHuidData();
            }
        });
    }

    private setMessage(message: string, type: string) {
        this.errorMessage = message;
        this.showMessage = true;
        this.messageClass = type;
        setTimeout(() => {
            this.showMessage = false;
        }, 4000);
    }

    getPdfWithSelectedDateRange() {
        let requestBody: HuidRequestBody = {
            from:  new Date(this.dateGroup.value.fromDate),
            to: new Date(this.dateGroup.value.toDate),
            applyDateRangeOn: this.dateGroup.value.dateRangeOn,
            includeSaledData: this.includeSaledData,
            includeStockData: this.includeInstockData
        }

        this.huidService.downloadPDF(requestBody);
    }
}

