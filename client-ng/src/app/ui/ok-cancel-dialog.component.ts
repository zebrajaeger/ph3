import {Component, EventEmitter, Output} from '@angular/core';

@Component({
    selector: 'app-ok-cancel-dialog',
    templateUrl: './ok-cancel-dialog.component.html',
    styleUrls: ['./ok-cancel-dialog.component.scss'],
    host: {'class': 'modal2'}
})
export class OkCancelDialogComponent {
    @Output() public onOk = new EventEmitter<any>();
    @Output() public onCancel = new EventEmitter<any>();

    public message: string = '';
    public visible: boolean = false;
    public id: any;

    public show(id: any, message: string) {
        this.id = id;
        this.message = message;
        this.visible = true;
    }

    _onOk() {
        this.onOk.emit(this.id);
        this.visible = false;
    }

    _onCancel() {
        this.onCancel.emit(this.id);
        this.visible = false;
    }
}
