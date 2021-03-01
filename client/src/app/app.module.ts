import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MainScreenComponent } from './main-screen/main-screen.component';
import { StatusbarComponent } from './statusbar/statusbar.component';
import { TitlebarComponent } from './titlebar/titlebar.component';
import { FullPartialChooserComponent } from './full-partial-chooser/full-partial-chooser.component';
import { ButtonComponent } from './button/button.component';
import { BorderChooserComponent } from './border-chooser/border-chooser.component';
import { FovDisplayComponent } from './fov-display/fov-display.component';
import { PanoramaScreenComponent } from './panorama-screen/panorama-screen.component';
import { PictureScreenComponent } from './picture-screen/picture-screen.component';
import { PlayScreenComponent } from './play-screen/play-screen.component';
import { PanoStatusGridComponent } from './pano-status-grid/pano-status-grid.component';

@NgModule({
  declarations: [
    AppComponent,
    MainScreenComponent,
    StatusbarComponent,
    TitlebarComponent,
    FullPartialChooserComponent,
    ButtonComponent,
    BorderChooserComponent,
    FovDisplayComponent,
    PanoramaScreenComponent,
    PictureScreenComponent,
    PlayScreenComponent,
    PanoStatusGridComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
