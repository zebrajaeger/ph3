import {Component, OnDestroy, OnInit} from '@angular/core';
import {JoystickService} from '../joystick.service';
import {JoystickPosition} from '../../data/joystick';
import {Subscription} from 'rxjs';
import {RouterService} from '../router.service';
import {UiService} from '../ui.service';
import {PanoService} from '../pano.service';

@Component({
    selector: 'app-joystick',
    templateUrl: './joystick.component.html',
    styleUrls: ['./joystick.component.scss']
})
export class JoystickComponent implements OnInit, OnDestroy {
    public joystickPosition!: JoystickPosition;
    public joystickPositionSubscription!: Subscription;

    constructor(private joystickService: JoystickService,
                private panoService: PanoService,
                private routerService: RouterService,
                private uiService: UiService) {
        routerService.onActivate(this, () => this.onActivate());
    }

    ngOnInit(): void {
        this.joystickPositionSubscription =
            this.joystickService
                .position()
                .subscribe(joystickPosition => this.joystickPosition = joystickPosition);
    }

    ngOnDestroy(): void {
        if (this.joystickPositionSubscription) {
            this.joystickPositionSubscription.unsubscribe();
        }
    }

    onCenter(): void {
        this.joystickService.center();
    }

    onReset(): void {
        this.joystickService.reset();
    }

    private onActivate(): void {
        this.uiService.title.next('Joystick');
        this.uiService.backButton.next(true);
        this.panoService.requestRecalculatePano();
    }
}
