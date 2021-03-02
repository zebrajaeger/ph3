import {InjectionToken, NgModule} from '@angular/core';
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
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NumberInputComponent } from './number-input/number-input.component';
import { PanoSettingsScreenComponent } from './pano-settings-screen/pano-settings-screen.component';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {OverlayModule} from '@angular/cdk/overlay';
import {MatButtonModule} from '@angular/material/button'

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
    PanoStatusGridComponent,
    NumberInputComponent,
    PanoSettingsScreenComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    OverlayModule,
    MatDialogModule,
    MatButtonModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
