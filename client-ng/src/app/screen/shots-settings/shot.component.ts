import {Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {Shot} from "../../../data/camera";
import { OkCancelDialogComponent } from 'src/app/ui/ok-cancel-dialog.component';
import { ModalService } from 'src/app/ui/modal.service';

@Component({
    selector: 'shot',
    templateUrl: './shot.component.html',
    styleUrls: ['./shot.component.scss']
})
export class ShotComponent {
    @Input() public index: number = 0;
    @Input() public shot!: Shot;
    @Input() public first = false;
    @Input() public last = false;
    @Input() public editable = false;
    @Output() public onChange = new EventEmitter<number>();
    @Output() public onDelete = new EventEmitter<number>();
    @Output() public onDown = new EventEmitter<number>();
    @Output() public onUp = new EventEmitter<number>();

    public edit: string | undefined = undefined;
    public temp!: number;
    
    constructor(public modalService: ModalService){
        
    }
    _onDown() {
        this.onDown.emit(this.index);
    }

    _onUp() {
        this.onUp.emit(this.index);
    }

    _onDelete() {
        this.onDelete.emit(this.index);
    }

    _onEditFocus() {
        this.edit = 'f';
        this.temp = this.shot.focusTimeMs / 1000;
        this.modalService.open('shot');
    }

    _onEditTrigger() {
        this.edit = 't';
        this.temp = this.shot.triggerTimeMs / 1000;
        this.modalService.open('shot');
    }

    onEditorClose() {
        if (this.edit === 'f') {
            this.shot.focusTimeMs = this.temp * 1000;
            this.onChange.emit(this.index)
        } else if (this.edit === 't') {
            this.shot.triggerTimeMs = this.temp * 1000;
            this.onChange.emit(this.index)
        }
        this.edit = undefined;
        }
}
