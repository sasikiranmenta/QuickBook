import {Injectable} from '@angular/core';
import {HttpService} from './http.service';
import {Observable} from 'rxjs';
import {HttpParams} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ViewInvoicesService {
  constructor(private httpService: HttpService) {
  }

  public getInvoicesInBetween(from: Date, to: Date): Observable<any> {
    const httpParams = new HttpParams();
    httpParams.append('fromDate', from.getDate());
    httpParams.append('toDate', to.getDate());
    return this.httpService.get('/quick-book/getAllInvoiceInBetween?fromDate='+from.toLocaleDateString()+'&toDate='+to.toLocaleDateString());
  }
}
