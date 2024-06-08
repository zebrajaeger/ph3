import {AfterViewInit, Component} from '@angular/core';
import {DownloadService} from "../service/download.service";
import {RouterService} from "../service/router.service";
import {UiService} from "../service/ui.service";

@Component({
    selector: 'app-download',
    templateUrl: './download.component.html',
    styleUrls: ['./download.component.scss']
})
export class DownloadComponent implements AfterViewInit {
    fileNames: string[] = [];

    constructor(private routerService: RouterService,
                private uiService: UiService,
                private download: DownloadService) {
        routerService.onActivate(this, () => this.onActivate());
    }

    ngAfterViewInit(): void {
        this.download.requestPapywizardFiles(fileNames => {
            this.fileNames = fileNames;
        })
    }

    private onActivate(): void {
        this.uiService.title.next('Download');
        this.uiService.backButton.next(true);
    }
}
