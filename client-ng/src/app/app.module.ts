import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {
    InjectableRxStompConfig,
    InjectableRxStompRPCConfig,
    RxStompRPCService,
    RxStompService,
    rxStompServiceFactory
} from '@stomp/ng2-stompjs';
import {myRxStompConfig} from './my-rx-stomp.config';
import {myRxStompRtcConfig} from './my-rx-stomp-rpc.config';
import {JoystickComponent} from './screen/joystick.component';
import {CameraComponent} from './screen/camera.component';
import {PictureFovComponent} from './screen/picture-fov.component';
import {PanoFovComponent} from './screen/pano-fov.component';
import {RecordComponent} from './screen/record.component';
import {MainComponent} from './screen/main.component';
import {TopComponent} from './bar/top.component';
import {BottomComponent} from './bar/bottom.component';
import {UiService} from './ui.service';
import {PanoService} from './pano.service';
import {CameraService} from './camera.service';
import {ConnectionService} from './connection.service';
import {JoystickService} from './joystick.service';
import {PanoHeadService} from './panohead.service';
import {RouterService} from './router.service';
import {NumberPadComponent} from './ui/number-pad.component';
import {ModalComponent} from './ui/modal.component';
import {ModalService} from './ui/modal.service';
import { PanoSettingsComponent } from './screen/pano-settings.component';
import { MsToSecondPipe } from './ms-to-second.pipe';
import { FormatSecondPipe } from './format-second.pipe';
import { AlignComponent } from './screen/align.component';

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
        MsToSecondPipe,
        FormatSecondPipe,
        AlignComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule
    ],
    providers: [
        {
            provide: InjectableRxStompConfig,
            useValue: myRxStompConfig,
        },
        {
            provide: InjectableRxStompRPCConfig,
            useValue: myRxStompRtcConfig,
        },
        {
            provide: RxStompService,
            useFactory: rxStompServiceFactory,
            deps: [InjectableRxStompConfig],
        }, {
            provide: RxStompRPCService,
            useFactory: (config: InjectableRxStompRPCConfig, service: RxStompService) => new RxStompRPCService(service, config),
            deps: [InjectableRxStompRPCConfig, RxStompService],
        },
        CameraService,
        ConnectionService,
        JoystickService,
        PanoService,
        PanoHeadService,
        RouterService,
        UiService,
        ModalService
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
