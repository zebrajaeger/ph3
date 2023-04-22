import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {UiService} from '../service/ui.service';
import {Router} from '@angular/router';
import {ConnectionService} from '../service/connection.service';
import {CameraService} from "../service/camera.service";
import {Camera} from "../../data/camera";

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

  public camera_?: Camera;
  private cameraSubscription!: Subscription;

  public cameraActivityIndicator: string = "green";

  constructor(private router: Router,
              private connectionService: ConnectionService,
              private cameraService: CameraService,
              private uiService: UiService) {
  }

  ngOnInit(): void {
    this.titleSubscription = this.uiService.title.subscribe(title => this.title = title);
    this.backButtonSubscription = this.uiService.backButton.subscribe(enabled => this.backButton = enabled);
    this.stateNameSubscription = this.connectionService.stateName().subscribe(stateName => this.stateName = stateName);
    this.cameraSubscription = this.cameraService.subscribeCamera(camera => this.camera = camera);
    this.cameraService.requestCamera(camera => this.camera = camera);
  }

  ngOnDestroy(): void {
    this.titleSubscription.unsubscribe();
    this.backButtonSubscription.unsubscribe();
    this.stateNameSubscription.unsubscribe();
    this.cameraSubscription.unsubscribe();
  }

  onBack(): void {
    this.router.navigate(['/']).then();
  }

  private set camera(camera: Camera | undefined) {

    this.camera_ = camera;
    if (this.camera_?.trigger) {
      this.cameraActivityIndicator = "red"
    } else if (this.camera_?.focus) {
      this.cameraActivityIndicator = "amber"
    } else {
      this.cameraActivityIndicator = "green"
    }
  }

  public get camera(): Camera | undefined {
    return this.camera_;
  }
}
