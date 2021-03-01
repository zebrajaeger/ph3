import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MainScreenComponent} from './main-screen/main-screen.component';
import {PanoramaScreenComponent} from './panorama-screen/panorama-screen.component';
import {PictureScreenComponent} from './picture-screen/picture-screen.component';
import {PlayScreenComponent} from './play-screen/play-screen.component';

const routes: Routes = [
  {path: 'panorama', component: PanoramaScreenComponent},
  {path: 'picture', component: PictureScreenComponent},
  {path: 'play', component: PlayScreenComponent},
  {path: '**', component: MainScreenComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
