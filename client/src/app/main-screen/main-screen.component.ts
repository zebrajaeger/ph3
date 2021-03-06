import {Component, OnInit} from '@angular/core';
import {StateService} from '../state.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-main-screen',
  templateUrl: './main-screen.component.html',
  styleUrls: ['./main-screen.component.scss']
})
export class MainScreenComponent implements OnInit {

  constructor(private router: Router, private stateService: StateService) {
    const self = this;
    stateService.currentRouterComponentSubject.subscribe(screen => {
      if (screen === self) {
        stateService.title = 'Main';
        stateService.backDisabled = true;
      }
    });
  }

  ngOnInit(): void {
  }

  onPicture(): void {
    this.router.navigate(['/picture']);
  }

  onPanoFOV(): void {
    this.router.navigate(['/panorama']);
  }

  onPanoSettings(): void {
    this.router.navigate(['/pano-settings']);
  }

  onPlay(): void {
    this.router.navigate(['/play']);
  }

  onPlaySettings(): void {
    this.router.navigate(['/play-settings']);
  }
  onServerSettings(): void {
    this.router.navigate(['/server-settings']);
  }
}
