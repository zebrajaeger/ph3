import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {
  ComponentActivation,
  ComponentActivationListener,
  NavigationBackEnableListener,
  RouterService
} from '../router.service';
import {TitlebarService} from '../titlebar.service';
import {Observable, Subject} from 'rxjs';

export abstract class StatusBarTitle {
  abstract get statusBarTitle(): string;
}

@Component({
  selector: 'app-titlebar',
  templateUrl: './titlebar.component.html',
  styleUrls: ['./titlebar.component.scss']
})
export class TitlebarComponent implements OnInit {
  private _title = 'Ich bin der Titel';
  _backEnabled = true;
  _saveEnabled = true;
  _backObservable = new Subject<void>();
  private _saveObservable = new Subject<void>();

  constructor(private router: Router,
              private routerService: RouterService,
              private titlebarService: TitlebarService) {
  }

  ngOnInit(): void {
    // this.routerService.registerComponentActivationListener(this);
    this.titlebarService.titlebar = this;
  }
  //
  // onComponentActivated(component: any): void {
  //   if (component instanceof StatusBarTitle) {
  //     this.title = component.statusBarTitle;
  //   }
  // }

  // onComponentDeactivated(component: any): void {
  //   this.titlebarService.title = '';
  // }

  set title(value: string) {
    this._title = value;
  }

  get title(): string {
    return this._title;
  }

  get saveObservable(): Subject<void> {
    return this._saveObservable;
  }

  get backObservable(): Subject<void> {
    return this._backObservable;
  }

  set backEnabled(enabled: boolean) {
    this._backEnabled = enabled;
  }

  set saveEnabled(enabled: boolean) {
    this._saveEnabled = enabled;
  }

  onBack(): void {
    this.router.navigate(['/']).then();
    this._backObservable.next();
  }

  onSave(): void {
    this._saveObservable.next();
  }
}
