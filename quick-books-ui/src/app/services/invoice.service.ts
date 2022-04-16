import {Injectable} from '@angular/core';
import {HttpService} from './http.service';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class InvoiceService {
  constructor(private httpService: HttpService) {
  }

  public persistInvoice(invoice, print: boolean, isEditMode: boolean): Observable<any> {
    let path = '';
    if(isEditMode) {
        path = print ? '/quick-book/updateInvoice?print=1' : '/quick-book/updateInvoice?print=0';
    } else {
        path = print ? '/quick-book/saveInvoice?print=1' : '/quick-book/saveInvoice?print=0';
    }
    const options = {
      observe: 'response', responseType: 'blob'
    };
    return this.httpService.post(path, invoice, print ? options : undefined);
  }

  public getInvoiceNumber(): Observable<any> {
    return this.httpService.get('/quick-book/getInvoiceNumber');
  }

  public getInvoice(id: number): Observable<any> {
      return this.httpService.get('/quick-book/getInvoice?invoice_id='+id);
  }

  public downloadPDF(response) {
    const file = new Blob([response.body], {type: 'application/pdf'});
    const fileURL = URL.createObjectURL(file);
    // if you want to open PDF in new tab
    window.open(fileURL);
    const a = document.createElement('a');
    a.href = fileURL;
    a.target = '_blank';
    a.download = response.headers.get('file_name');
    document.body.appendChild(a);
    a.click();
  }
}
