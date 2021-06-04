import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {UiService} from '../ui.service';
import {RouterService} from '../router.service';
import {PanoHeadService} from '../panohead.service';

@Component({
    selector: 'app-main',
    templateUrl: './main.component.html',
    styleUrls: ['./main.component.scss']
})
export class MainComponent implements OnInit {
    constructor(private router: Router,
                private routerService: RouterService,
                private panoHeadService: PanoHeadService,
                private uiService: UiService) {
        this.routerService.onActivate(this, () => this.onActivate());
    }

    ngOnInit(): void {
    }

    onJoystick(): void {
        this.router.navigate(['/joystick']);
    }

    onCamera(): void {
        this.router.navigate(['/camera']);
    }

    onPictureFOV(): void {
        this.router.navigate(['/picture-fov']);
    }

    onPanoFOV(): void {
        this.router.navigate(['/pano-fov']);
    }

    onRecord(): void {
        this.router.navigate(['/record']);
    }

    private onActivate(): void {
        this.uiService.title.next('main');
        this.uiService.backButton.next(false);
        this.panoHeadService.jogging(false);
    }
}
