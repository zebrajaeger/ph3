import {Component, OnInit} from '@angular/core';
import {DeviceService} from '../device.service';
import {MatDialog} from '@angular/material/dialog';
import {NumberInputComponent} from '../number-input/number-input.component';
import {StatusBarTitle} from '../titlebar/titlebar.component';
import {TitlebarService} from '../titlebar.service';
import {RouterService} from '../router.service';
import {PanoService, PanoSettings} from '../pano.service';
import {first} from 'rxjs/operators';

@Component({
  selector: 'app-pano-settings-screen',
  templateUrl: './pano-settings-screen.component.html',
  styleUrls: ['./pano-settings-screen.component.scss']
})
export class PanoSettingsScreenComponent implements OnInit {

  settings: PanoSettings = {minOverlapX: 25.0, minOverlapY: 25.0};

  constructor(private titlebarService: TitlebarService,
              private routerService: RouterService,
              private panoService: PanoService,
              public dialog: MatDialog) {

    panoService.panoSettingsObservable.subscribe(settings => this.settings = settings);

    routerService.onActivate(this, () => {
      titlebarService.title = 'Pano settings';
      titlebarService.backEnabled = true;
      titlebarService.saveEnabled = true;
    });

    titlebarService.onSave(() => {
      if (routerService.isActive(this)) {
        this.panoService.panoSettings = this.settings;
      }
    });
  }

  ngOnInit(): void {
  }

  chooseX(): void {
    const dialogRef = this.dialog.open(NumberInputComponent, {
      data: {value: this.settings.minOverlapX}
    });

    dialogRef.afterClosed().pipe(first()).subscribe(result => {
      if (result !== undefined) {
        this.settings.minOverlapX = result.value;
      }
    });
  }

  chooseY(): void {
    const dialogRef = this.dialog.open(NumberInputComponent, {
      data: {value: this.settings.minOverlapY}
    });

    dialogRef.afterClosed().pipe(first()).subscribe(result => {
      if (result !== undefined) {
        this.settings.minOverlapY = result.value;
      }
    });
  }
}
