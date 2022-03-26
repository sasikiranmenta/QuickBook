import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HttpService {

  constructor(private httpClient: HttpClient) {
  }

  get(urlPath: string): Observable<any> {
    return this.httpClient.get(urlPath);
  }

  post(urlPath: string, body: any = {}): Observable<any> {
    return this.httpClient.post(urlPath, body);
  }

  put(urlPath: string, body: any = {}): Observable<any> {
    return this.httpClient.put(urlPath, body);
  }

  delete(urlPath: string): Observable<any> {
    return this.httpClient.delete(urlPath);
  }
}
