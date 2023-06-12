import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {JoystickComponent} from './screen/joystick.component';
import {PictureFovComponent} from './screen/picture-fov/picture-fov.component';
import {PanoFovComponent} from './screen/pano-fov.component';
import {CameraComponent} from './screen/camera.component';
import {MainComponent} from './screen/main.component';
import {RecordComponent} from './screen/record.component';
import {AlignComponent} from './screen/align.component';
import {DownloadComponent} from "./screen/download.component";
import {SavePictureFovPresetComponent} from "./screen/picture-fov/save-picture-fov-preset.component";
import {LoadPictureFovPresetComponent} from "./screen/picture-fov/load-picture-fov-preset.component";
import {EditPictureFovPresetComponent} from "./screen/picture-fov/edit-picture-fov-preset.component";
import {CalcFovComponent} from "./screen/calc-fov.component";
import {MovementSettingsComponent} from "./screen/movement-settings/movement-settings.component";
import {ShotsSettingsComponent} from "./screen/shots-settings/shots-settings.component";

const routes: Routes = [
    {path: 'align', component: AlignComponent},
    {path: 'joystick', component: JoystickComponent},
    {path: 'camera', component: CameraComponent},
    {path: 'calc-fov', component: CalcFovComponent},
    {path: 'picture-fov', component: PictureFovComponent},
    {path: 'picture-fov/save', component: SavePictureFovPresetComponent},
    {path: 'picture-fov/load', component: LoadPictureFovPresetComponent},
    {path: 'picture-fov/edit', component: EditPictureFovPresetComponent},
    {path: 'pano-fov', component: PanoFovComponent},
    {path: 'movement-settings', component: MovementSettingsComponent},
    {path: 'pano-fov', component: PanoFovComponent},
    {path: 'record', component: RecordComponent},
    {path: 'shots', component: ShotsSettingsComponent},
    {path: 'download', component: DownloadComponent},
    {path: '**', component: MainComponent}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
