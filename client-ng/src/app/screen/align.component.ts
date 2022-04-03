import {Component, OnDestroy, OnInit} from '@angular/core';
import {JoystickPosition} from '../../data/joystick';
import {Subscription} from 'rxjs';
import {JoystickService} from '../joystick.service';
import {RouterService} from '../router.service';
import {UiService} from '../ui.service';
import {PanoHeadService} from '../panohead.service';

@Component({
    selector: 'app-align',
    templateUrl: './align.component.html',
    styleUrls: ['./align.component.scss']
})
export class AlignComponent implements OnInit, OnDestroy {
    public joystickPosition: JoystickPosition;
    public joystickPositionSubscription: Subscription;

    constructor(private routerService: RouterService,
                private joystickService: JoystickService,
                private panoHeadService: PanoHeadService,
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
        this.uiService.title.next('Align Panohead');
        this.uiService.backButton.next(true);
        this.panoHeadService.sendJogging(true);
    }

    setAsZero(): void {
        this.panoHeadService.sendSetToZero();
    }
}
