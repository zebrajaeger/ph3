import {Component, OnDestroy, OnInit} from '@angular/core';
import {Border, FieldOfViewPartial, PanoMatrix} from '../../data/pano';
import {Subscription} from 'rxjs';
import {PanoService} from '../service/pano.service';
import {PanoHeadService} from '../service/panohead.service';
import {RouterService} from '../service/router.service';
import {UiService} from '../service/ui.service';
import {ConnectionService} from '../service/connection.service';
import {degToString} from '../utils';
import {ModalService} from "../ui/modal.service";

@Component({
  selector: 'app-pano-fov',
  templateUrl: './pano-fov.component.html',
  styleUrls: ['./pano-fov.component.scss']
})
export class PanoFovComponent implements OnInit, OnDestroy {
  private _fov_!: FieldOfViewPartial | undefined;
  public panoMatrix: PanoMatrix | undefined;
  private fovSubscription!: Subscription;

  private openSubscription!: Subscription;
  private calculatedPanoSubscription!: Subscription;

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
    this.fovSubscription = this.panoService.subscribePanoFov(fov => this.fov = fov);
    this.calculatedPanoSubscription = this.panoService.subscribePanoMatrix(panoMatrix => this.panoMatrix = panoMatrix);
  }

  ngOnDestroy(): void {
    this.openSubscription?.unsubscribe();
    this.fovSubscription?.unsubscribe();
    this.calculatedPanoSubscription?.unsubscribe();
  }

  get fov(): FieldOfViewPartial|undefined {
    return this._fov_;
  }

  set fov(fov: FieldOfViewPartial|undefined) {
    this._fov_ = fov;
    if(!fov){
      return;
    }

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
    this.panoService.setPanoBorder(Border.TOP);
  }

  onLeft(): void {
    this.panoService.setPanoBorder(Border.LEFT);
  }

  onRight(): void {
    this.panoService.setPanoBorder(Border.RIGHT);
  }

  onBottom(): void {
    this.panoService.setPanoBorder(Border.BOTTOM);
  }

  onFullX(): void {
    this.panoService.setPanoFullX(!this.fov?.fullX);
  }
  onFullY(): void {
    this.panoService.setPanoFullY(!this.fov?.fullY);
  }

  private onActivate(): void {
    this.uiService.title.next('Pano FOV');
    this.uiService.backButton.next(true);
    this.panoHeadService.sendJogging(true);

    this.panoService.requestPanoFov(fov => this.fov = fov);
    this.panoService.requestRecalculatePano();
  }

  onClosePopup() {
    this.modalService.close('pano-fov-set');
  }
}
