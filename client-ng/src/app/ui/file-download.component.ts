import {Component, Input} from '@angular/core';
import {DownloadService} from "../service/download.service";

@Component({
    selector: 'file-download',
    templateUrl: './file-download.component.html',
    styleUrls: ['./file-download.component.scss']
})
export class FileDownloadComponent {
    @Input()
    public fileName!: string;

    constructor(private downloadService: DownloadService) {
    }

    onDownload() {
        this.downloadService.requestFile(this.fileName, content => {
            // https://stackoverflow.com/questions/52154874/angular-6-downloading-file-from-rest-api#answer-52687792
            const newBlob = new Blob([content], {type: "text/plain"});

            const data = window.URL.createObjectURL(newBlob);
            const link = document.createElement('a');
            link.href = data;
            link.download = this.fileName;
            // this is necessary as link.click() does not work on the latest firefox
            link.dispatchEvent(new MouseEvent('click', {bubbles: true, cancelable: true, view: window}));

            setTimeout(function () {
                // For Firefox it is necessary to delay revoking the ObjectURL
                window.URL.revokeObjectURL(data);
                link.remove();
            }, 100);
        })
    }
}
