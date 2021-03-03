import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {DeviceService} from '../device.service';
import {StateService} from '../state.service';
import {MatDialog} from '@angular/material/dialog';
import {NumberInputComponent} from '../number-input/number-input.component';

@Component({
  selector: 'app-play-settings-screen',
  templateUrl: './play-settings-screen.component.html',
  styleUrls: ['./play-settings-screen.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PlaySettingsScreenComponent {

  settings = {
    beforeMoveDelay: 0.0,
    afterMoveDelay: 0.0,
    focusTime: 1.0,
    triggerTime: 1.0
  };

  constructor(private dataConnectionService: DeviceService,
              private stateService: StateService,
              public dialog: MatDialog,
              private changeDetector: ChangeDetectorRef) {
    const self = this;
    stateService.currentRouterComponentSubject.subscribe(screen => {
      if (screen === self) {
        stateService.title = 'Play Settings';
        stateService.backDisabled = false;
      }
    });
  }

  chooseBeforeMoveDelay(): void {
    this.chooseValue(this.settings.beforeMoveDelay).then(newValue => {
      this.settings.beforeMoveDelay = newValue;
      this.changeDetector.markForCheck();
    });
  }

  chooseAfterMoveDelay(): void {
    this.chooseValue(this.settings.afterMoveDelay).then(newValue => {
      this.settings.afterMoveDelay = newValue;
      this.changeDetector.markForCheck();
    });
  }

  chooseTriggerTime(): void {
    this.chooseValue(this.settings.triggerTime).then(newValue => {
      this.settings.triggerTime = newValue;
      this.changeDetector.markForCheck();
    });
  }

  chooseFocusTime(): void {
    this.chooseValue(this.settings.focusTime).then(newValue => {
      this.settings.focusTime = newValue;
      this.changeDetector.markForCheck();
    });
  }

  async chooseValue(currentValue: number): Promise<number> {
    return new Promise(resolve => {
      const dialogRef = this.dialog.open(NumberInputComponent, {
        data: {value: currentValue}
      });

      dialogRef.afterClosed().subscribe(result => {
        if (result !== undefined) {
          resolve(result.value);
        } else {
          resolve(currentValue);
        }
      });
    });
  }
}
