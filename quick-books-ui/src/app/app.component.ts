import {Component, OnInit} from '@angular/core';
import {AlertController} from "@ionic/angular";
import {WebSocketApi} from "./services/websocket.api.service";


@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent implements OnInit{
    alertWindowEl: HTMLIonAlertElement = null;
    constructor(public alertController: AlertController, private websocket: WebSocketApi) {}

    ngOnInit(): void {
        this.connect();
    }

    connect() {
        this.websocket.statusUpdate.subscribe((status) => {
            if ('false' ===  status) {
                if(this.alertWindowEl === null) {
                    this.alertController.create({
                        header: 'Internet not available', buttons: [], backdropDismiss: false
                    }).then((presentEl) => {
                        presentEl.present();
                        this.alertWindowEl = presentEl;
                    });
                }
            } else {
                if (this.alertWindowEl !== null) {
                    this.alertWindowEl.dismiss();
                    this.alertWindowEl = null;
                }
            }
        });
    };

}
