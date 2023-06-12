import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
    selector: 'app-ok-cancel-dialog',
    templateUrl: './ok-cancel-dialog.component.html',
    styleUrls: ['./ok-cancel-dialog.component.scss'],
    host: {'class': 'modal'}
})
export class OkCancelDialogComponent {
    @Input() public message!: string;
    @Output() public onOk = new EventEmitter<void>();
    @Output() public onCancel = new EventEmitter<void>();

    _onOk() {
        this.onOk.emit();
    }

    _onCancel() {
        this.onCancel.emit();
    }
}
