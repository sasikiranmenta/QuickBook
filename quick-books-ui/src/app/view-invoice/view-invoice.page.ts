import {Component, OnInit, ViewChild} from '@angular/core';
import {
    ColDef,
    FilterChangedEvent,
    GridReadyEvent,
    RowDataChangedEvent, RowDataUpdatedEvent,
    RowDoubleClickedEvent
} from 'ag-grid-community';
import {ViewInvoicesService} from '../services/view.invoices.service';
import {AgGridAngular} from 'ag-grid-angular';
import {IndianCurrency} from '../pipe/indian-currency.pipe';
import {DatePipe, formatDate} from '@angular/common';
import {UntypedFormControl, UntypedFormGroup, Validators} from '@angular/forms';
import {Invoice, TotalValue} from '../invoice/invoice';
import {AlertController, PopoverController} from '@ionic/angular';
import {Router} from '@angular/router';
import {EmailComponent} from './email/email.component';
import {SelectedInvoice} from "./SelectedInvoice";

@Component({
    selector: 'app-view-invoice', templateUrl: './view-invoice.page.html', styleUrls: ['./view-invoice.page.scss'],
})
export class ViewInvoicePage implements OnInit {

    @ViewChild('agGrid') agGrid: AgGridAngular;

    selectedInvoices: Array<SelectedInvoice> = new Array<SelectedInvoice>();

    includeGstBills = true;
    showOnlyGstBills = false;
    showMessage = false;
    messageClass = '';
    errorMessage = '';
    emailLoading = false;

    template = '<span class="ag-overlay-loading-center">Please wait while your rows are loading</span>';

    dateGroup: UntypedFormGroup;

    silverDetails: TotalValue = {
        totalAfterTax: 0, totalBeforeTax: 0, totalCgst: 0, totalIgst: 0, totalSgst: 0, totalWeight: 0
    };
    goldDetails: TotalValue = {
        totalAfterTax: 0, totalBeforeTax: 0, totalCgst: 0, totalIgst: 0, totalSgst: 0, totalWeight: 0
    };


    public columnDefs: ColDef[] = [{
        headerName: 'Invoice No.',
        field: 'invoiceId',
        filter: 'agSetColumnFilter',
        checkboxSelection: true,
        headerCheckboxSelection: true
    }, {
        headerName: 'HSN', field: 'invoiceType', filter: 'agSetColumnFilter'
    }, {
        headerName: 'Date',
        field: 'billDate',
        filter: 'agDateColumnFilter',
        valueFormatter: (param) => this.datePipe.transform(param.value, 'dd/MM/YYYY')
    }, {
        headerName: 'FY',
        field: 'financialYear',
        filter: 'agSetColumnFilter',
        valueFormatter: (param) => {
            let temp = +param.value;
            temp++;
            return param.value + '-' + temp;
        }
    },
        {
            headerName: 'Weight', field: 'totalWeight', filter: 'agSetColumnFilter'
        }, {
            headerName: 'Before Tax',
            field: 'amountBeforeTax',
            filter: 'agSetColumnFilter',
            valueFormatter: (param) => this.indianCurrency.transform(param.value, false)
        }, {
            headerName: 'CGST',
            field: 'cgstAmount',
            filter: 'agSetColumnFilter',
            valueFormatter: (param) => this.indianCurrency.transform(param.value, false)
        }, {
            headerName: 'SGST',
            field: 'sgstAmount',
            filter: 'agSetColumnFilter',
            valueFormatter: (param) => this.indianCurrency.transform(param.value, false)
        }, {
            headerName: 'After Tax',
            field: 'totalAmountAfterTax',
            filter: 'agSetColumnFilter',
            valueFormatter: (param) => this.indianCurrency.transform(param.value, false)
        }];

    rowData: Array<any>;
    defaultColDef: ColDef = {
        flex: 1, resizable: true
    };


    constructor(private viewInvoiceService: ViewInvoicesService,
                private indianCurrency: IndianCurrency,
                private datePipe: DatePipe,
                private alertController: AlertController,
                private router: Router,
                private popoverController: PopoverController) {
    }

    ngOnInit() {
        this.intializeTotalsToZero();
        const currentDate = new Date();
        const financialStartDate = this.getFinancialStartDate(currentDate);
        this.getInvoiceData(financialStartDate, currentDate, this.includeGstBills, this.showOnlyGstBills);
        this.initForm(financialStartDate, currentDate);
    }

    onGridReady(params: GridReadyEvent) {
    }

    onRowEdit($event: RowDoubleClickedEvent) {

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
                    text: 'Print',
                    role: 'print',
                    cssClass: 'secondary',
                    handler: () => {
                        this.alertController.dismiss(null, 'print');
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
            if (dismisEl.role === 'edit') {
                this.router.navigateByUrl('/invoice/edit/' + $event.data.invoiceId + '/' + $event.data.financialYear);
            } else if (dismisEl.role === 'print') {
                this.selectedInvoices = [];
                this.selectedInvoices.push({invoiceId: $event.data.invoiceId, financialYear: $event.data.financialYear});
                this.getPdfWithSelectedInvoices();
            }
        });
    }

    onRowDataChanged($event: RowDataUpdatedEvent) {
        console.log("changed");
        this.setTotal();
    }

    getInvoiceData(from: Date, to: Date, includeGst: boolean, showOnlyGst: boolean) {
        this.viewInvoiceService.getInvoicesInBetween(from, to, includeGst, showOnlyGst).subscribe((response) => {
            this.rowData = response;
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

    onDateChange() {
        if (this.dateGroup.valid) {
            const fromDate: Date = new Date(this.dateGroup.value.fromDate);
            const toDate: Date = new Date(this.dateGroup.value.toDate);
            this.getInvoiceData(fromDate, toDate, this.includeGstBills, this.showOnlyGstBills);
            this.intializeTotalsToZero();
        }
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
            toDate: new UntypedFormControl(formatDate(currentDate, 'yyyy-MM-dd', 'en'), Validators.required)
        });
    }

    onCheckBoxChange() {
        this.setSelectedInvoices();
    }

    setSelectedInvoices() {
        this.selectedInvoices = new Array<SelectedInvoice>();
        this.agGrid.api.getSelectedRows().forEach((invoice: Invoice) => {
            this.selectedInvoices.push({invoiceId: invoice.invoiceId, financialYear: invoice.financialYear});
        });
    }

    getPdfWithSelectedInvoices() {
        if (this.selectedInvoices.length !== 0) {
            this.viewInvoiceService.downloadSelectedInvoices(this.selectedInvoices);
            this.agGrid.api.deselectAll();
        } else {
            this.setMessage('Nothing selected to download', 'error');
        }
    }

    showOnlyGstBillsCheckBoxChange() {
        if (!this.showOnlyGstBills) {
            this.includeGstCheckBoxChange(false);
        } else {
            this.setGstIncludedData(this.includeGstBills, !this.showOnlyGstBills);
        }
    }

    includeGstCheckBoxChange(fromTemplate: boolean) {
        if (fromTemplate && this.showOnlyGstBills) {
            if (this.showOnlyGstBills && this.includeGstBills) {
                this.showOnlyGstBills = false;
            }
        } else if (!fromTemplate) {
            if (!this.showOnlyGstBills === true) {
                this.includeGstBills = true;
            }
            this.setGstIncludedData(this.includeGstBills, !this.showOnlyGstBills);
            return;
        }
        this.setGstIncludedData(!this.includeGstBills, this.showOnlyGstBills);
    }

    setGstIncludedData(includeGst: boolean, showOnlyGst: boolean) {
        if (this.dateGroup.valid) {
            const fromDate: Date = new Date(this.dateGroup.value.fromDate);
            const toDate: Date = new Date(this.dateGroup.value.toDate);
            this.getInvoiceData(fromDate, toDate, includeGst, showOnlyGst);
            this.intializeTotalsToZero();
        }
    }

    sendEmail(event) {
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
                this.viewInvoiceService
                    .sendSummaryReportMail(data.data, new Date(this.dateGroup.value.fromDate), new Date(this.dateGroup.value.toDate))
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

    private setMessage(message: string, type: string) {
        this.errorMessage = message;
        this.showMessage = true;
        this.messageClass = type;
        setTimeout(() => {
            this.showMessage = false;
        }, 4000);
    }
}

