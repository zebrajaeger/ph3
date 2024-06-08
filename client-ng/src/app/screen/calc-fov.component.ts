import {Component} from '@angular/core';
import {RouterService} from "../service/router.service";
import {UiService} from "../service/ui.service";
import {ModalService} from "../ui/modal.service";

@Component({
    selector: 'app-calc-fov',
    templateUrl: './calc-fov.component.html',
    styleUrls: ['./calc-fov.component.scss']
})
export class CalcFovComponent {
    public xSize = 22.4;
    public ySize = 15;
    public focalDistance = 24;
    overlap = 0.25;

    public dSize = 0;
    public xFov = 0;
    public yFov = 0;
    public dFov = 0;
    public nX = 0;
    public nY = 0;
    public nSum = 0;

    public id: string = '';
    public temp: number = 0;

    constructor(private routerService: RouterService,
                private uiService: UiService,
                public modalService: ModalService) {
        routerService.onActivate(this, () => this.onActivate());
    }

    private onActivate(): void {
        this.uiService.title.next('Calc FOV');
        this.uiService.backButton.next(true);
        this.calculate();
    }

    calculate() {
        this.dSize = Math.sqrt((this.xSize * this.xSize) + (this.ySize * this.ySize));
        this.xFov = 180 / Math.PI * 2 * Math.atan(this.xSize / (2 * this.focalDistance));
        this.nX = 360 / (this.xFov * (1 - this.overlap));
        this.yFov = 180 / Math.PI * 2 * Math.atan(this.ySize / (2 * this.focalDistance));
        this.nY = 180 / (this.yFov * (1 - this.overlap));
        this.dFov = 180 / Math.PI * 2 * Math.atan(this.dSize / (2 * this.focalDistance));
        this.nSum = Math.ceil(this.nX) * Math.ceil(this.nY);
    }

    onEdit(id: string) {
        this.id = id;
        switch (id) {
            case 'x':
                this.temp = this.xSize;
                break;
            case 'y':
                this.temp = this.ySize;
                break;
            case 'f':
                this.temp = this.focalDistance;
                break;
            case 'o':
                this.temp = this.overlap;
                break;
        }
        this.modalService.open('calc');
    }

    onEditorClose() {
        switch (this.id) {
            case 'x':
                this.xSize = this.temp;
                break;
            case 'y':
                this.ySize = this.temp;
                break;
            case 'f':
                this.focalDistance = this.temp;
                break;
            case 'o':
                if (this.temp > 1) this.overlap = 1
                else this.overlap = this.temp;
                break;
        }
        this.calculate();
    }
}
