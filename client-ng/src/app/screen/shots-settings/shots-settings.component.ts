import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {UiService} from "../../service/ui.service";
import {RouterService} from "../../service/router.service";
import {Shots, ShotsPresets} from "../../../data/camera";
import {ShotService} from "../../service/shot.service";
import {Subscription} from "rxjs";
import {OkCancelDialogComponent} from "../../ui/ok-cancel-dialog.component";
import {KeyboardDialogComponent} from "../../ui/keyboard-dialog.component";

@Component({
    selector: 'app-shots-settings',
    templateUrl: './shots-settings.component.html',
    styleUrls: ['./shots-settings.component.scss']
})
export class ShotsSettingsComponent implements OnInit, OnDestroy {
    public _shots: Shots = new Shots();
    public _presets: ShotsPresets = new ShotsPresets();

    public tempShots!: Shots;
    public editPreset: string | undefined;

    private editMode = '';

    @ViewChild('okcancel')
    private okCancelDialog!: OkCancelDialogComponent;
    @ViewChild('keyboard')
    private keyboardDialog!: KeyboardDialogComponent;

    constructor(private uiService: UiService,
                private routerService: RouterService,
                private shotService: ShotService) {
        this.routerService.onActivate(this, () => this.onActivate());
    }

    private shotsSubscription!: Subscription;
    private shotsPresetsSubscription!: Subscription;

    ngOnInit(): void {
        this.shotsSubscription = this.shotService.subscribeCurrentShots(shots => this.shots = shots);
        this.shotService.requestCurrentShots(shots => this.shots = shots);

        this.shotsPresetsSubscription = this.shotService.subscribeShotsPresets(presets => this.presets = presets);
        this.shotService.requestShotsPresets(presets => this.presets = presets);
    }

    ngOnDestroy() {
        this.shotsSubscription?.unsubscribe();
    }

    set shots(value: Shots) {
        this._shots = value;
    }

    set presets(value: ShotsPresets) {
        this._presets = value;
    }

    private onActivate(): void {
        this.uiService.title.next('Shot Settings');
        this.uiService.backButton.next(true);
    }

    onCurrentShotChanged(shots: Shots) {
        this.shots = shots;
        this.shotService.sendSetCurrentShots(this._shots)
    }

    _onDeletePreset(key: string) {
        this.okCancelDialog.show(key, `Delete preset '${key}?'`);
    }

    _onDeletePresetOk(key: string) {
        this._presets.delete(key);
    }

    _onLoadPreset(name: string) {
        this._shots = <Shots>this._presets.get(name);
        this.shotService.sendSetCurrentShots(this._shots)
    }

    _onEditPreset(name: string) {
        this.editPreset = name;
        this.tempShots = <Shots>this._presets.get(name)?.clone();
    }

    _onAddPreset() {
        this.editMode = 'addEmptyPreset';
        this.keyboardDialog.show('Name for new Preset', '');
    }

    _onEditPresetOk() {
        this._presets.set(<string>this.editPreset, this.tempShots);
        this.editPreset = undefined;
        this.shotService.sendSetPresets(this._presets);
    }

    _onEditPresetCancel() {
        this.editPreset = undefined;
    }

    onAsNewPreset() {
        this.editMode = 'asNewPreset';
        this.keyboardDialog.show('Enter new shot-preset name', '')
    }

    onKeyboardOk(name: string) {
        if (this.editMode === 'addEmptyPreset') {
            this.editPreset = name;
            this.tempShots = new Shots();

        } else if (this.editMode === 'asNewPreset') {
            this._presets.set(name, this._shots.clone());
            this.shotService.sendSetPresets(this._presets)
        }
    }
}
