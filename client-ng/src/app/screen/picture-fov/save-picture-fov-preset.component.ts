import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import Keyboard from "simple-keyboard";
import {layouts} from "../../utils";
import {FieldOfView} from "../../../data/pano";
import {Subscription} from "rxjs";
import {PanoService} from "../../service/pano.service";
import {RouterService} from "../../service/router.service";
import {UiService} from "../../service/ui.service";

@Component({
    selector: 'app-save-picture-fov-preset',
    templateUrl: './save-picture-fov-preset.component.html',
    styleUrls: ['./save-picture-fov-preset.component.scss']
})
export class SavePictureFovPresetComponent implements AfterViewInit, OnInit,OnDestroy {
    value = "";
    keyboard!: Keyboard;

    public fov_!: FieldOfView;
    private fovSubscription!: Subscription;

    constructor(private panoService: PanoService,
                private routerService: RouterService,
                private uiService: UiService) {
        this.routerService.onActivate(this, () => this.onActivate());
    }

    ngOnInit() {
        this.fovSubscription = this.panoService.subscribePictureFov(fov => this.fov = fov);
    }

    ngOnDestroy() {
        this.fovSubscription?.unsubscribe();
    }

    ngAfterViewInit() {
        this.keyboard = new Keyboard({
            onChange: input => this.onChange(input),
            onKeyPress: button => this.onKeyPress(button),
            layout: layouts.de
        });
    }

    private onActivate(): void {
        this.uiService.title.next('Save Camera FOV Preset');
        this.uiService.backButton.next(true);
        this.panoService.requestPictureFov(fov => this.fov = fov);
    }

    set fov(fov: FieldOfView) {
        this.fov_ = fov;
    }

    onChange = (input: string) => {
        this.value = input;
    };

    onKeyPress = (button: string) => {
        if (button === "{shift}" || button === "{lock}") this.handleShift();
    };

    onInputChange = (event: any) => {
        this.keyboard.setInput(event.target.value);
    };

    handleShift = () => {
        let currentLayout = this.keyboard.options.layoutName;
        let shiftToggle = currentLayout === "default" ? "shift" : "default";

        this.keyboard.setOptions({
            layoutName: shiftToggle
        });
    };

    onOk() {
        this.panoService.setCurrentPictureFovAs(this.value);
    }
}
