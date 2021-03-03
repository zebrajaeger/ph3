import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MainScreenComponent} from './main-screen/main-screen.component';
import {PanoramaScreenComponent} from './panorama-screen/panorama-screen.component';
import {PictureScreenComponent} from './picture-screen/picture-screen.component';
import {PlayScreenComponent} from './play-screen/play-screen.component';
import {PanoSettingsScreenComponent} from './pano-settings-screen/pano-settings-screen.component';
import {PlaySettingsScreenComponent} from './play-settings-screen/play-settings-screen.component';

const routes: Routes = [
  {path: 'picture', component: PictureScreenComponent},
  {path: 'panorama', component: PanoramaScreenComponent},
  {path: 'pano-settings', component: PanoSettingsScreenComponent},
  {path: 'play', component: PlayScreenComponent},
  {path: 'play-settings', component: PlaySettingsScreenComponent},
  {path: '**', component: MainScreenComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
