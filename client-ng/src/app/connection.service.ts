import {Injectable} from '@angular/core';
import {RxStompService} from '@stomp/ng2-stompjs';
import {RxStompState} from '@stomp/rx-stomp/esm6/rx-stomp-state';
import {BehaviorSubject, Observable, Subscription} from 'rxjs';
import {filter} from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class ConnectionService {
    private connectionStateSubscription: Subscription;
    private stateName_ = new BehaviorSubject<string>(RxStompState[RxStompState.CLOSED]);
    private open_ = new BehaviorSubject<boolean>(false);

    constructor(private rxStompService: RxStompService) {
        this.connectionStateSubscription = this.rxStompService.connectionState$.subscribe(state => {
            this.stateName_.next(RxStompState[state]);
            this.open_.next(state === RxStompState.OPEN);
        });
    }

    public open(): Observable<boolean> {
        return this.open_
            .pipe(filter(isOpen => isOpen));
    }

    public stateName(): BehaviorSubject<string> {
        return this.stateName_;
    }
}
