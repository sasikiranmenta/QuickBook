import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HttpService {
  baseUrl = '';
  constructor(private httpClient: HttpClient) {
    // this.baseUrl = 'http://'+ window.location.host;
    this.baseUrl = 'http://localhost:8081';
  }

  get(urlPath: string, options?: any ): Observable<any> {
    if(!!options) {
      console.log('params flow');
      return this.httpClient.get(this.baseUrl+urlPath, {params: options.params});
    }
    return this.httpClient.get(this.baseUrl+urlPath);
  }

  post(urlPath: string, body: any = {}, options: any = {}): Observable<any> {
    return this.httpClient.post(this.baseUrl + urlPath, body, options);
  }

  put(urlPath: string, body: any = {}): Observable<any> {
    return this.httpClient.put(this.baseUrl+urlPath, body);
  }

  delete(urlPath: string): Observable<any> {
    return this.httpClient.delete(this.baseUrl+urlPath);
  }
}
