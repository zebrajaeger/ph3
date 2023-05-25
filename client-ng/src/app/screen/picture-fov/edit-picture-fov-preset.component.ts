import {Component, OnDestroy, OnInit} from '@angular/core';
import {PanoService} from "../../service/pano.service";
import {RouterService} from "../../service/router.service";
import {UiService} from "../../service/ui.service";

@Component({
    selector: 'app-edit-picture-fov-preset',
    templateUrl: './edit-picture-fov-preset.component.html',
    styleUrls: ['./edit-picture-fov-preset.component.scss']
})
export class EditPictureFovPresetComponent implements OnInit, OnDestroy {
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

    set names(value: string[]) {
        console.log('VALUES', value)
        this.names_ = value;
    }

    private onActivate(): void {
        this.uiService.title.next('Edit Camera FOV Preset');
        this.uiService.backButton.next(true);
        this.panoService.requestPictureFovNames(names => this.names = names);
    }

    onDelete(name: string) {
        this.panoService.deletePictureFov(name);
    }

    onRename(ignoredName: string) {
        // TODO
    }
}
