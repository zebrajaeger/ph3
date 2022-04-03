import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {JoystickComponent} from './screen/joystick.component';
import {PictureFovComponent} from './screen/picture-fov.component';
import {PanoFovComponent} from './screen/pano-fov.component';
import {CameraComponent} from './screen/camera.component';
import {MainComponent} from './screen/main.component';
import {RecordComponent} from './screen/record.component';
import {PanoSettingsComponent} from './screen/pano-settings.component';
import {AlignComponent} from './screen/align.component';

const routes: Routes = [
    {path: 'align', component: AlignComponent},
    {path: 'joystick', component: JoystickComponent},
    {path: 'camera', component: CameraComponent},
    {path: 'picture-fov', component: PictureFovComponent},
    {path: 'pano-fov', component: PanoFovComponent},
    {path: 'pano-settings', component: PanoSettingsComponent},
    {path: 'pano-fov', component: PanoFovComponent},
    {path: 'record', component: RecordComponent},
    {path: '**', component: MainComponent}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
