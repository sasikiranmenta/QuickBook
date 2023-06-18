import {Injectable} from '@angular/core';
import {HttpService} from './http.service';
import {Observable} from 'rxjs';
import {HuidItem} from "../huid/item/huidItem";
import {HuidRequestBody} from "../huid/item/huid-request-body";
import {HuidResponse} from "../huid/item/huidResponse";

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

  public getAllHuid(requestBody: HuidRequestBody): Observable<HuidResponse> {
    return this.httpService.post('/quick-book/huid/fetchAllHuid', requestBody);
  }

  public downloadPDF(requestBody: HuidRequestBody) {

      let path = '/quick-book/huid/downloadData';

      const options = {
          observe: 'response', responseType: 'blob'
      };
      return this.httpService.post(path, requestBody, options).subscribe((response: any) => {
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
      });
  }

    public sendEmail(requestBody: HuidRequestBody, emailId: string): Observable<any> {
        return this.httpService.post('/quick-book/huid/emailSummary?emailId='+emailId, requestBody);
    }

}
