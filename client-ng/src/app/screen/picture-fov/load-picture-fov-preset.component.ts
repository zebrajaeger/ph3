import {Component, OnDestroy, OnInit} from '@angular/core';
import {PanoService} from "../../service/pano.service";
import {RouterService} from "../../service/router.service";
import {UiService} from "../../service/ui.service";

@Component({
    selector: 'app-load-picture-fov-preset',
    templateUrl: './load-picture-fov-preset.component.html',
    styleUrls: ['./load-picture-fov-preset.component.scss']
})
export class LoadPictureFovPresetComponent implements OnInit, OnDestroy {
    names_: string[] = [];
    private fovNamesSubscription: any;

    constructor(private panoService: PanoService,
                private routerService: RouterService,
                private uiService: UiService) {
        this.routerService.onActivate(this, () => this.onActivate());
    }

    ngOnInit() {
        this.fovNamesSubscription = this.panoService.subscribePictureFovNames(names => this.names = names);
        this.panoService.requestPictureFovNames(names => this.names = names);
    }

    ngOnDestroy() {
        this.fovNamesSubscription?.unsubscribe();
    }

    set names(value : string[]){
        console.log('VALUES', value)
        this.names_ = value;
    }

    private onActivate(): void {
        this.uiService.title.next('Load Camera FOV Preset');
        this.uiService.backButton.next(true);
        this.panoService.requestPictureFovNames(names => this.names = names);
    }

    onLoad(name: string) {
        this.panoService.loadPictureFov(name);
    }
}
