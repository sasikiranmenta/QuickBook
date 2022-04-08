import {Component, OnInit, ViewChild} from '@angular/core';
import {ColDef,  FilterChangedEvent,  GridReadyEvent, RowDataChangedEvent,  RowDoubleClickedEvent } from 'ag-grid-community';
import {ViewInvoicesService} from '../services/view.invoices.service';
import {AgGridAngular} from 'ag-grid-angular';
import {IndianCurrency} from '../pipe/indian-currency.pipe';
import { DatePipe, formatDate} from '@angular/common';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {TotalValue} from '../invoice/invoice';

@Component({
  selector: 'app-view-invoice',
  templateUrl: './view-invoice.page.html',
  styleUrls: ['./view-invoice.page.scss'],
})
export class ViewInvoicePage implements OnInit {

  @ViewChild('agGrid') agGrid: AgGridAngular;

  dateGroup: FormGroup;

  silverDetails: TotalValue = {
    totalAfterTax: 0,
    totalBeforeTax: 0,
    totalCgst: 0,
    totalIgst: 0,
    totalSgst: 0,
    totalWeight: 0
  };
  goldDetails: TotalValue = {
    totalAfterTax: 0,
    totalBeforeTax: 0,
    totalCgst: 0,
    totalIgst: 0,
    totalSgst: 0,
    totalWeight: 0
  };


  public columnDefs: ColDef[] = [
    {
      headerName: 'Invoice No.',
      field: 'invoiceId',
      filter: 'agSetColumnFilter',
    },
    {
      headerName: 'HSN',
      field: 'invoiceType',
      filter: 'agSetColumnFilter'
    },
    {
      headerName: 'Date',
      field: 'billDate',
      filter: 'agDateColumnFilter',
      valueFormatter: (param) => this.datePipe.transform(param.value, 'dd/MM/YYYY')
    },
    {
      headerName: 'Weight',
      field: 'totalWeight',
      filter: 'agSetColumnFilter'
    },
    {
      headerName: 'Before Tax',
      field: 'amountBeforeTax',
      filter: 'agSetColumnFilter',
      valueFormatter: (param) => this.indianCurrency.transform(param.value, false)
    }, {
      headerName: 'CGST',
      field: 'cgstAmount',
      filter: 'agSetColumnFilter',
      valueFormatter: (param) => this.indianCurrency.transform(param.value,false)
    }, {
      headerName: 'SGST',
      field: 'sgstAmount',
      filter: 'agSetColumnFilter',
      valueFormatter: (param) => this.indianCurrency.transform(param.value, false)
    },
    {
      headerName: 'After Tax',
      field: 'totalAmountAfterTax',
      filter: 'agSetColumnFilter',
      valueFormatter: (param) => this.indianCurrency.transform(param.value, false)
    }
  ];

  rowData: Array<any>;
  defaultColDef: ColDef = {
    flex: 1,
    resizable: true
  };


  constructor(private viewInvoiceService: ViewInvoicesService, private indianCurrency: IndianCurrency,
              private datePipe: DatePipe) {
  }

  ngOnInit() {
    this.intializeTotalsToZero();
    const currentDate = new Date();
    const financialStartDate = this.getFinancialStartDate(currentDate);
    this.getInvoiceData(financialStartDate, currentDate);
    this.initForm(financialStartDate, currentDate);
  }

  onGridReady(params: GridReadyEvent) {
    console.log(params);
  }

  onRowEdit($event: RowDoubleClickedEvent) {
    console.log($event);
  }

  private getInvoiceData(from: Date, to: Date) {
    this.viewInvoiceService.getInvoicesInBetween(from, to).subscribe((response) => {
      this.rowData = response;
    });
  }

  private getFinancialStartDate(currentDate: Date) {
    let previousYear: number = currentDate.getFullYear();
    console.log(currentDate.getUTCMonth());
    if (currentDate.getUTCMonth() < 3) {
      previousYear = previousYear - 1;
    }
    return new Date(previousYear, 3, 1);
  }

  private onFilterChange($event: FilterChangedEvent) {
    this.setTotal();
  }

  private setTotal() {
    this.intializeTotalsToZero();
    this.agGrid.api.forEachNodeAfterFilter((rowNode, index) => {
      if(rowNode.data.invoiceType === 'GOLD') {
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

  private onDateChange() {
     if (this.dateGroup.valid) {
       const fromDate: Date = new Date(this.dateGroup.value.fromDate);
       const toDate: Date = new Date(this.dateGroup.value.toDate);
       this.getInvoiceData(fromDate, toDate);
       this.intializeTotalsToZero();
     }
  }

  private intializeTotalsToZero() {
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

  private initForm(financialStartDate: Date, currentDate: Date) {
    this.dateGroup = new FormGroup({
      fromDate: new FormControl(formatDate(financialStartDate, 'yyyy-MM-dd', 'en'), Validators.required),
      toDate: new FormControl(formatDate(currentDate, 'yyyy-MM-dd', 'en'), Validators.required)
    });
  }

  onRowDataChanged($event: RowDataChangedEvent) {
    this.setTotal();
    console.log('row data changed');
  }
}

const filterParams = {
  comparator(a: string, b: string) {
    const valA = parseInt(a);
    const valB = parseInt(b);
    if (valA === valB) {return 0;}
    return valA > valB ? 1 : -1;
  },
};

function getRowData() {
  const rows = [];
  for (let i = 1; i < 117; i++) {
    rows.push({age: i});
  }
  return rows;

}
