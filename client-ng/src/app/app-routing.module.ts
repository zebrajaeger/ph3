import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {JoystickComponent} from './screen/joystick.component';
import {PictureFovComponent} from './screen/picture-fov.component';
import {PanoFovComponent} from './screen/pano-fov.component';
import {CameraComponent} from './screen/camera.component';
import {MainComponent} from './screen/main.component';

const routes: Routes = [
    {path: 'joystick', component: JoystickComponent},
    {path: 'camera', component: CameraComponent},
    {path: 'picture-fov', component: PictureFovComponent},
    {path: 'pano-fov', component: PanoFovComponent},
    {path: 'pano-fov', component: PanoFovComponent},
    {path: '**', component: MainComponent}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}