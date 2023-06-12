import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Shot, Shots} from "../../../data/camera";

@Component({
    selector: 'shots',
    templateUrl: './shots.component.html',
    styleUrls: ['./shots.component.scss']
})
export class ShotsComponent {
    @Input()
    public set shots(value: Shots) {
        this._shots = value;
    }

    @Input() public editable = false;
    @Output() public onChange = new EventEmitter<Shots>();

    public _shots: Shots = new Shots();
    public showDeleteOkCancelDialog = false;
    public shotToDeleteId = -1;
    public deleteMessage: string = '';

    onUp(index: number) {
        const temp = this._shots[index];
        this._shots[index] = this._shots[index - 1]
        this._shots[index - 1] = temp
        this.onChange.emit(this._shots);
    }

    onDown(index: number) {
        const temp = this._shots[index];
        this._shots[index] = this._shots[index + 1]
        this._shots[index + 1] = temp
        this.onChange.emit(this._shots);
    }

    onAdd() {
        this._shots.push(new Shot())
        this.onChange.emit(this._shots);
    }

    onDelete(index: number) {
        this.shotToDeleteId = index;
        this.deleteMessage = `Delete shot #${index} (F:${this._shots[index].focusTimeMs / 1000}, T:${this._shots[index].triggerTimeMs / 1000})?`
        this.showDeleteOkCancelDialog = true;
    }

    _onDeleteShotOk() {
        this.showDeleteOkCancelDialog = false;
        this._shots.splice(this.shotToDeleteId, 1);
        this.onChange.emit(this._shots);
    }

    _onDeleteShotCancel() {
        this.showDeleteOkCancelDialog = false;
    }
}
