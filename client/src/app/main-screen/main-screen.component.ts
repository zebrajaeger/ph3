import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ComponentActivation, ComponentActivationListener, RouterService} from '../router.service';
import {TitlebarService} from '../titlebar.service';

@Component({
  selector: 'app-main-screen',
  templateUrl: './main-screen.component.html',
  styleUrls: ['./main-screen.component.scss']
})
export class MainScreenComponent implements OnInit {

  constructor(private router: Router,
              private routerService: RouterService,
              private titlebarService: TitlebarService) {
    this.routerService.onActivate(this, () => {
      this.titlebarService.backEnabled = false;
      this.titlebarService.saveEnabled = false;
      this.titlebarService.title = 'Main';
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

  onSandbox(): void {
    this.router.navigate(['/sandbox']);
  }
}
