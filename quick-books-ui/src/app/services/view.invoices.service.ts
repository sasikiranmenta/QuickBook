import {Injectable} from '@angular/core';
import {HttpService} from './http.service';
import {Observable} from 'rxjs';
import {HttpParams} from '@angular/common/http';
import {InvoiceService} from './invoice.service';

@Injectable({
  providedIn: 'root'
})
export class ViewInvoicesService {
  constructor(private httpService: HttpService, private invoiceService: InvoiceService) {
  }

  public getInvoicesInBetween(from: Date, to: Date,includeGst: boolean, showOnlyGst: boolean): Observable<any> {
    const httpParams = new HttpParams();
    httpParams.append('fromDate', from.getDate());
    httpParams.append('toDate', to.getDate());
    return this.httpService.
    get('/quick-book/getAllInvoiceInBetweenBasedOnGst?fromDate='+from.toLocaleDateString()+
        '&toDate='+to.toLocaleDateString()+'&includeGst='+includeGst+'&showOnlyGst='+showOnlyGst, undefined);
  }

  public downloadSelectedInvoices(invoices: Array<number>): void {
      const path = '/quick-book/getBills';
      const options = {
          observe: 'response', responseType: 'blob'
      };
      this.httpService.post(path, invoices, options).subscribe((response) => {
          this.invoiceService.downloadPDF(response);
      });
  }

  public sendSummaryReportMail(emailId: string, from: Date, to: Date) {
      const path = '/quick-book/emailSummary';
      const httpParams: HttpParams = new HttpParams();
      httpParams.append('fromDate', from.getDate());
      httpParams.append('toDate', to.getDate());
      httpParams.append('emailId', emailId);
      return this.httpService.get(path+'?fromDate='+from.toLocaleDateString()+
          '&toDate='+to.toLocaleDateString()+'&emailId='+emailId);
  }
}
