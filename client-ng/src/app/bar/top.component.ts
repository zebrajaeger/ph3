import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {UiService} from '../ui.service';
import {Router} from '@angular/router';
import {ConnectionService} from '../connection.service';

@Component({
    selector: 'app-top',
    templateUrl: './top.component.html',
    styleUrls: ['./top.component.scss']
})
export class TopComponent implements OnInit, OnDestroy {
    public stateName: string;
    private stateNameSubscription: Subscription;

    public title: string;
    private titleSubscription: Subscription;

    public backButton: boolean;
    private backButtonSubscription: Subscription;

    constructor(private router: Router, private connectionService: ConnectionService, private uiService: UiService) {
    }

    ngOnInit(): void {
        this.titleSubscription = this.uiService.title.subscribe(title => this.title = title);
        this.backButtonSubscription = this.uiService.backButton.subscribe(enabled => this.backButton = enabled);
        this.stateNameSubscription = this.connectionService.stateName().subscribe(stateName => this.stateName = stateName);
    }

    ngOnDestroy(): void {
        this.titleSubscription.unsubscribe();
        this.backButtonSubscription.unsubscribe();
        this.stateNameSubscription.unsubscribe();
    }

    onBack(): void {
        this.router.navigate(['/']);
    }
}
