import {Injectable} from '@angular/core';
import {Subscription} from "rxjs";
import {map} from "rxjs/operators";
import {RxStompRPCService} from "./rx-stomp-rpc.service";

@Injectable({
    providedIn: 'root'
})
export class DownloadService {

    constructor(private rxStompRPCService: RxStompRPCService) {
    }

    public requestPapywizardFiles(cb: (content: string[]) => void): Subscription {
        return this.rxStompRPCService
            .rpc({destination: '/rpc/files/papywizard/files'})
            .pipe(map(msg => JSON.parse(msg.body)))
            .subscribe(cb);
    }

    public requestFile(fileName: string, cb: (content: string) => void): Subscription {
        return this.rxStompRPCService
            .rpc({destination: '/rpc/files/papywizard/download', body: fileName})
            .pipe(map(msg => JSON.parse(msg.body)))
            .subscribe(cb);
    }
}
