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
import { MatDialogModule} from '@angular/material/dialog';
import {OverlayModule} from '@angular/cdk/overlay';
import {MatButtonModule} from '@angular/material/button';
import { PlaySettingsScreenComponent } from './play-settings-screen/play-settings-screen.component';
import { TextInputComponent } from './text-input/text-input.component';
import { ServerSettingsScreenComponent } from './server-settings-screen/server-settings-screen.component'
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';

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
    PlaySettingsScreenComponent,
    TextInputComponent,
    ServerSettingsScreenComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    OverlayModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
