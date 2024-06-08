import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {PanoService} from "../../service/pano.service";
import {RouterService} from "../../service/router.service";
import {UiService} from "../../service/ui.service";
import {OkCancelDialogComponent} from "../../ui/ok-cancel-dialog.component";
import {KeyboardDialogComponent} from "../../ui/keyboard-dialog.component";

@Component({
    selector: 'app-edit-picture-fov-preset',
    templateUrl: './edit-picture-fov-preset.component.html',
    styleUrls: ['./edit-picture-fov-preset.component.scss']
})
export class EditPictureFovPresetComponent implements OnInit, OnDestroy {
    names_: string[] = [];
    private fovNamesSubscription: any;
    oldName!: string;

    @ViewChild('okcancel')
    private okCancelDialog!: OkCancelDialogComponent;
    @ViewChild('keyboard')
    private keyboardDialog!: KeyboardDialogComponent;

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
        this.names_ = value;
    }

    private onActivate(): void {
        this.uiService.title.next('Edit Camera FOV Preset');
        this.uiService.backButton.next(true);
        this.panoService.requestPictureFovNames(names => this.names = names);
    }

    onDelete(name: string) {
        this.okCancelDialog.show(name, `Delete preset '${name}'?`);
    }

    onRename(name: string) {
        this.oldName = name;
        this.keyboardDialog.show('Rename FOV preset to', name);
    }

    onKeyboardDialogOk(name: string) {
        this.panoService.renamePictureFov(this.oldName, name);
    }

    onOkCancelDialogOk(key: string) {
        this.panoService.deletePictureFov(key);
    }
}
