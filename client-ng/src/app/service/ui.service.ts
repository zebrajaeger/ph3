import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class UiService {

    public title: BehaviorSubject<string> = new BehaviorSubject<string>('');
    public backButton: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

    constructor() {
    }
}
