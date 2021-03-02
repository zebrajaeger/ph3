import {Component, OnInit} from '@angular/core';
import {DeviceService} from '../device.service';
import {StateService} from '../state.service';
import {MatDialog} from '@angular/material/dialog';
import {NumberInputComponent} from '../number-input/number-input.component';

@Component({
  selector: 'app-pano-settings-screen',
  templateUrl: './pano-settings-screen.component.html',
  styleUrls: ['./pano-settings-screen.component.scss']
})
export class PanoSettingsScreenComponent implements OnInit {

  overlap = {x: 25.0, y: 25.0};

  constructor(private dataConnectionService: DeviceService, private stateService: StateService, public dialog: MatDialog) {
    const self = this;
    stateService.currentRouterComponentSubject.subscribe(screen => {
      if (screen === self) {
        stateService.title = 'Panorama Settings';
        stateService.backDisabled = false;
      }
    });
  }

  ngOnInit(): void {
  }

  chooseX(): void {
    const dialogRef = this.dialog.open(NumberInputComponent, {
      data: {value: this.overlap.x}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result !== undefined) {
        this.overlap.x = result.value;
      }
    });
  }

  chooseY(): void {
    const dialogRef = this.dialog.open(NumberInputComponent, {
      data: {value: this.overlap.y}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result !== undefined) {
        this.overlap.y = result.value;
      }
    });
  }
}
