import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { UiService } from '../service/ui.service';
import { Router } from '@angular/router';
import { ConnectionService } from '../service/connection.service';
import { CameraService } from "../service/camera.service";
import { Camera } from "../../data/camera";
import { PanoHeadService } from "../service/panohead.service";
import { Version } from "../../data/system";
import { SystemService } from "../service/system.service";

@Component({
    selector: 'app-top',
    templateUrl: './top.component.html',
    styleUrls: ['./top.component.scss']
})
export class TopComponent implements OnInit, OnDestroy {
    public stateName!: string;
    private stateNameSubscription!: Subscription;

    public title!: string;
    private titleSubscription!: Subscription;

    public backButton!: boolean;
    private backButtonSubscription!: Subscription;

    private cameraSubscription!: Subscription;
    public cameraActivityIndicator: string = "green";

    private actorActiveSubscription!: Subscription;
    private _actorActive = false;
    public actorActiveIndicator: string = "green";

    private isJoggingSubscription!: Subscription;
    private _isJogging = false;

    public version: Version | undefined = undefined;

    constructor(private router: Router,
        private connectionService: ConnectionService,
        private cameraService: CameraService,
        private panoHead: PanoHeadService,
        private system: SystemService,
        private uiService: UiService) {
    }

    ngOnInit(): void {
        this.titleSubscription = this.uiService.title.subscribe(title => this.title = title);
        this.backButtonSubscription = this.uiService.backButton.subscribe(enabled => this.backButton = enabled);
        this.stateNameSubscription = this.connectionService.stateName().subscribe(stateName => this.stateName = stateName);
        this.cameraSubscription = this.cameraService.subscribeCamera(camera => this.camera = camera);
        this.cameraService.requestCamera(camera => this.camera = camera);
        this.actorActiveSubscription = this.panoHead.subscribeActorActive(isActive => this.actorActive = isActive);
        this.isJoggingSubscription = this.panoHead.subscribeJoggingState(isJogging => this.isJogging = isJogging);
        this.system.requestVersion(version => this.version = version);
    }

    ngOnDestroy(): void {
        this.titleSubscription.unsubscribe();
        this.backButtonSubscription.unsubscribe();
        this.stateNameSubscription.unsubscribe();
        this.cameraSubscription.unsubscribe();
        this.actorActiveSubscription.unsubscribe();
        this.isJoggingSubscription.unsubscribe();
    }

    private set camera(camera: Camera | undefined) {
        if (camera?.trigger) {
            this.cameraActivityIndicator = "red"
        } else if (camera?.focus) {
            this.cameraActivityIndicator = "amber"
        } else {
            this.cameraActivityIndicator = "green"
        }
    }

    private updateActiveIndicator(): void {
        if (this._actorActive) {
            this.actorActiveIndicator = "red";
        } else {
            this.actorActiveIndicator = this._isJogging ? "amber" : "green";
        }
    }

    private set actorActive(isActive: boolean) {
        console.log("actorActive", isActive)
        this._actorActive = isActive;
        this.updateActiveIndicator();
    }

    private set isJogging(isJogging: boolean) {
        console.log("isJogging", isJogging)
        this._isJogging = isJogging;
        this.updateActiveIndicator();
    }

    // protected readonly undefined = undefined; // TODO WTF is this? Art or garbage?
}
