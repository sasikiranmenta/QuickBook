import {Injectable} from '@angular/core';
import {HttpService} from './http.service';
import {Observable} from 'rxjs';
import {HuidItem} from "../huid/item/huidItem";

@Injectable({
  providedIn: 'root'
})
export class HuidService {
  constructor(private httpService: HttpService) {
  }

  public persistHuid(huid: HuidItem): Observable<HuidItem> {
    let path = '/quick-book/huid/saveHuid';
    return this.httpService.post(path, huid);
  }

  public getAllHuid(): Observable<Array<HuidItem>> {
    return this.httpService.get('/quick-book/huid/fetchAllHuid');
  }

  public getInvoice(id: number, financialYear: number): Observable<any> {
      return this.httpService.get('/quick-book/getInvoice?invoice_id='+id+'&financialYear='+financialYear);
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
