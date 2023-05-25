import {Component, OnDestroy, OnInit} from '@angular/core';
import {PanoService} from '../../service/pano.service';
import {Border, FieldOfView} from '../../../data/pano';
import {Subscription} from 'rxjs';
import {RouterService} from '../../service/router.service';
import {UiService} from '../../service/ui.service';
import {PanoHeadService} from '../../service/panohead.service';
import {ConnectionService} from '../../service/connection.service';
import {degToString} from '../../utils';
import {ModalService} from "../../ui/modal.service";

@Component({
  selector: 'app-picture-fov',
  templateUrl: './picture-fov.component.html',
  styleUrls: ['./picture-fov.component.scss']
})
export class PictureFovComponent implements OnInit, OnDestroy {
  private openSubscription!: Subscription;

  public fov_!: FieldOfView;
  private fovSubscription!: Subscription;

  public hFromText?: string;
  public hToText?: string;
  public hText?: string;

  public vText?: string;
  public vFromText?: string;
  public vToText?: string;

  constructor(private connectionService: ConnectionService,
              private panoService: PanoService,
              private panoHeadService: PanoHeadService,
              private routerService: RouterService,
              public modalService: ModalService,
              private uiService: UiService) {
    this.routerService.onActivate(this, () => this.onActivate());
  }

  ngOnInit(): void {
    this.openSubscription = this.connectionService.subscribeOpen(() => this.onActivate());
    this.fovSubscription = this.panoService.subscribePictureFov(fov => this.fov = fov);
  }

  ngOnDestroy(): void {
    this.openSubscription?.unsubscribe();
    this.fovSubscription?.unsubscribe();
  }

  set fov(fov: FieldOfView) {
    this.fov_ = fov;
    if (fov.horizontal.size) {
      this.hText = degToString(Math.abs(fov.horizontal.size));
    }
    this.hFromText = degToString(fov.horizontal.from);
    this.hToText = degToString(fov.horizontal.to);
    if (fov.vertical.size) {
      this.vText = degToString(Math.abs(fov.vertical.size));
    }
    this.vFromText = degToString(fov.vertical.from);
    this.vToText = degToString(fov.vertical.to);
  }

  onTop(): void {
    this.panoService.setPictureBorder(Border.TOP);
  }

  onLeft(): void {
    this.panoService.setPictureBorder(Border.LEFT);
  }

  onRight(): void {
    this.panoService.setPictureBorder(Border.RIGHT);
  }

  onBottom(): void {
    this.panoService.setPictureBorder(Border.BOTTOM);
  }

  private onActivate(): void {
    this.uiService.title.next('Camera FOV');
    this.uiService.backButton.next(true);
    this.panoHeadService.sendJogging(true);
    this.panoService.requestPictureFov(fov => this.fov = fov);
    this.panoService.requestRecalculatePano();
  }

  onClosePopup() {
    this.modalService.close('picture-fov-set');
  }
}
