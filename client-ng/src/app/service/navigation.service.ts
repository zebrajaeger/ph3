import { Injectable } from '@angular/core';
import { Location } from "@angular/common";
import { Router, NavigationEnd } from "@angular/router";

/**
 * <a href="https://nils-mehlhorn.de/posts/angular-navigate-back-previous-page/">
 *     https://nils-mehlhorn.de/posts/angular-navigate-back-previous-page/
 * </a>
 */
@Injectable({
  providedIn: 'root'
})
export class NavigationService {

  private history: string[] = [];

  constructor(private router: Router, private location: Location) {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.history.push(event.urlAfterRedirects);
      }
    });
  }

  back(): void {
    this.history.pop();
    if (this.history.length > 0) {
      this.location.back();
    } else {
      this.router.navigateByUrl("/").then();
    }
  }
}
