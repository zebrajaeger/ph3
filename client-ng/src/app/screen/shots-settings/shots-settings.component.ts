import {Component, OnDestroy, OnInit} from '@angular/core';
import {UiService} from "../../service/ui.service";
import {RouterService} from "../../service/router.service";
import {Shots, ShotsPresets} from "../../../data/camera";
import {ShotService} from "../../service/shot.service";
import {Subscription} from "rxjs";

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

    public showNewPresetDialog: boolean = false;
    public newPresetName!: string;

    public showDeleteOkCancelDialog: boolean = false;
    public deleteName!: string;
    deleteMessage: string = '';

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
        console.log('PRE', value)
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
        this.showDeleteOkCancelDialog = true;
        this.deleteName = key;
        this.deleteMessage = `Delete preset '${key}?'`
    }

    _onDeletePresetOk() {
        this.showDeleteOkCancelDialog = false;
        this._presets.delete(this.deleteName);
        this.deleteName = '';
    }

    _onDeletePresetCancel() {
        this.showDeleteOkCancelDialog = false;
        this.deleteName = '';
    }

    _onLoadPreset(key: string) {
        this._shots = <Shots>this._presets.get(key);
        this.shotService.sendSetCurrentShots(this._shots)
    }

    _onEditPreset(key: string) {
        this.editPreset = key;
        this.tempShots = <Shots>this._presets.get(key)?.clone();
    }

    _onEditPresetOk() {
        this._presets.set(<string>this.editPreset, this.tempShots);
        this.editPreset = undefined;
        this.shotService.sendSetPresets(this._presets);
    }

    _onEditPresetCancel() {
        this.editPreset = undefined;
    }


    // onKeyboardOk() {
    //     this.showNewPresetDialog = false;
    //     this._presets.set(this.newPresetName, this._shots.clone());
    //     this.shotService.sendSetPresets(this._presets);
    // }


    onAsNewPreset() {
        this.showNewPresetDialog = true;
        this.newPresetName = '';
    }

    onAsNewPresetOk() {
        this.showNewPresetDialog = false;
        this._presets.set(this.newPresetName, this._shots.clone());
        console.log("DEBUG",this._presets);
        this.shotService.sendSetPresets(this._presets)
    }

    onAsNewPresetCancel() {
        this.showNewPresetDialog = false;
    }
}
