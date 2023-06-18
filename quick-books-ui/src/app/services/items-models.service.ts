import {Injectable} from '@angular/core';
import {HttpService} from './http.service';
import {Observable} from 'rxjs';
import {HuidItem} from "../huid/item/huidItem";
import {HuidRequestBody} from "../huid/item/huid-request-body";
import {ItemModels} from "../huid/item/itemmodel";

@Injectable({
  providedIn: 'root'
})
export class ItemModelsService {
  constructor(private httpService: HttpService) {
  }

  public persistItemModel(itemModels: ItemModels): Observable<HuidItem> {
    let path = '/quick-book/itemModels/save';
    return this.httpService.post(path, itemModels);
  }

  public getItemModelsByItemType(type: 'GOLD' | 'SILVER'): Observable<ItemModels> {
      return this.httpService.get('/quick-book/itemModels/getByItemModel?itemModelType='+type);
  }

}
