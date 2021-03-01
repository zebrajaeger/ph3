import {Component, OnInit} from '@angular/core';
import {StateService} from '../state.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-titlebar',
  templateUrl: './titlebar.component.html',
  styleUrls: ['./titlebar.component.scss']
})
export class TitlebarComponent implements OnInit {

  _title = 'Ich bin der Titel';
  _backDisabled = false;

  constructor(private router: Router, private stateService: StateService) {
    stateService.titlebar = this;
  }

  ngOnInit(): void {
  }

  set title(title: string) {
    this._title = title;
  }

  set backDisabled(backDisabled: boolean) {
    this._backDisabled = backDisabled;
  }

  onBack(): void {
    this.router.navigate(['/']).then();
  }
}
