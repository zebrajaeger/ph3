import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {JoystickComponent} from './screen/joystick.component';
import {CameraComponent} from './screen/camera.component';
import {PictureFovComponent} from './screen/picture-fov.component';
import {PanoFovComponent} from './screen/pano-fov.component';
import {RecordComponent} from './screen/record.component';
import {MainComponent} from './screen/main.component';
import {TopComponent} from './bar/top.component';
import {BottomComponent} from './bar/bottom.component';
import {UiService} from './service/ui.service';
import {PanoService} from './service/pano.service';
import {CameraService} from './service/camera.service';
import {ConnectionService} from './service/connection.service';
import {JoystickService} from './service/joystick.service';
import {PanoHeadService} from './service/panohead.service';
import {RouterService} from './service/router.service';
import {SystemService} from './service/system.service';
import {NumberPadComponent} from './ui/number-pad.component';
import {ModalComponent} from './ui/modal.component';
import {ModalService} from './ui/modal.service';
import {PanoSettingsComponent} from './screen/pano-settings.component';
import {ToFixed} from './pipes/to-fixed';
import {AlignComponent} from './screen/align.component';
import {NgxJoystickModule} from "ngx-joystick";
import {MovementControllerComponent} from './ui/movement-controller.component';
import {MatrixComponent} from "./ui/matrix.component";
import {AddOnePipe} from "./pipes/add-one.pipe";
import { LedComponent } from './ui/led.component';
import {FovSizePipe} from "./pipes/Fov.pipe";
import {PositionPipe} from "./pipes/Position.pipe";
import {FovPartialSizePipe} from "./pipes/FovPartial.pipe";
import { Matrix2Component } from './ui/matrix2.component';

@NgModule({
  declarations: [
    AppComponent,
    JoystickComponent,
    CameraComponent,
    PictureFovComponent,
    PanoFovComponent,
    RecordComponent,
    MainComponent,
    TopComponent,
    BottomComponent,
    NumberPadComponent,
    ModalComponent,
    PanoSettingsComponent,
    FovSizePipe,
    FovPartialSizePipe,
    AddOnePipe,
    PositionPipe,
    ToFixed,
    AlignComponent,
    MovementControllerComponent,
    MatrixComponent,
    LedComponent,
    Matrix2Component
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgxJoystickModule
  ],
  providers: [
    CameraService,
    ConnectionService,
    JoystickService,
    PanoService,
    PanoHeadService,
    RouterService,
    UiService,
    ModalService,
    SystemService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
