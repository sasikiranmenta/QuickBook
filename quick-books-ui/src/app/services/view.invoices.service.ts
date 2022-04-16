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

  public getInvoicesInBetween(from: Date, to: Date): Observable<any> {
    const httpParams = new HttpParams();
    httpParams.append('fromDate', from.getDate());
    httpParams.append('toDate', to.getDate());
    return this.httpService.
    get('/quick-book/getAllInvoiceInBetween?fromDate='+from.toLocaleDateString()+'&toDate='+to.toLocaleDateString());
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
}
