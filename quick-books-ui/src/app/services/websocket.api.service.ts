import {Injectable} from "@angular/core";
import {Subject} from "rxjs";
import * as Stomp from 'stompjs';
import * as SockJs from 'sockjs-client';


@Injectable({
    providedIn: 'root'
})
export class WebSocketApi{
    statusUpdate = new Subject<string>();

    webSocketEndpoint: string = 'http://localhost:8081/ws';

    topic: string ='/topic';
    stompClient: any;

    constructor() {
        this.connect();
    }


    private connect() {
        let ws = new SockJs(this.webSocketEndpoint);
        this.stompClient = Stomp.over(ws);
        const _this = this;
        _this.stompClient.connect({},function(frame){
            _this.stompClient.subscribe(_this.topic,function(status){
                _this.updateStatus(status.body);
            });
        });
    }

    errorCallBack(error){
        console.log("error call back ->"+error)
        setTimeout(()=>{
            this.connect();
        },5000);
    }

    updateStatus(status){
        this.statusUpdate.next(status);
    }

}
