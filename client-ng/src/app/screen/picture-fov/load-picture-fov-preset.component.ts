import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {PanoService} from "../../service/pano.service";
import {RouterService} from "../../service/router.service";
import {UiService} from "../../service/ui.service";
import {OkCancelDialogComponent} from "../../ui/ok-cancel-dialog.component";
import {NavigationService} from "../../service/navigation.service";

@Component({
    selector: 'app-load-picture-fov-preset',
    templateUrl: './load-picture-fov-preset.component.html',
    styleUrls: ['./load-picture-fov-preset.component.scss']
})
export class LoadPictureFovPresetComponent implements OnInit, OnDestroy {
    names_: string[] = [];
    private fovNamesSubscription: any;

    @ViewChild('okcancel')
    private okCancelDialog!: OkCancelDialogComponent;

    constructor(private panoService: PanoService,
                private routerService: RouterService,
                private uiService: UiService,
                private navigation: NavigationService) {
        this.routerService.onActivate(this, () => this.onActivate());
    }

    ngOnInit() {
        this.fovNamesSubscription = this.panoService.subscribePictureFovNames(names => this.names = names);
        this.panoService.requestPictureFovNames(names => this.names = names);
    }

    ngOnDestroy() {
        this.fovNamesSubscription?.unsubscribe();
    }

    set names(value: string[]) {
        console.log('VALUES', value)
        this.names_ = value;
    }

    private onActivate(): void {
        this.uiService.title.next('Load Camera FOV Preset');
        this.uiService.backButton.next(true);
        this.panoService.requestPictureFovNames(names => this.names = names);
    }

    onLoad(name: string) {
        this.okCancelDialog.show(name, `Overwrite current FOV with '${name}' preset?`);
    }

    onOkCancelDialogOk(key: string) {
        this.panoService.loadPictureFov(key);
        this.navigation.back();
    }
}
