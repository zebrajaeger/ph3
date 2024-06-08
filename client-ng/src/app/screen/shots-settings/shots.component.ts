import {Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {Shot, Shots} from "../../../data/camera";
import {OkCancelDialogComponent} from "../../ui/ok-cancel-dialog.component";

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

    @ViewChild('okcancel')
    private okCancelDialog!: OkCancelDialogComponent;

    public _shots: Shots = new Shots();

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
        this.okCancelDialog.show(
            index,
            `Delete shot #${index} (F:${this._shots[index].focusTimeMs / 1000}, T:${this._shots[index].triggerTimeMs / 1000})?`)
    }

    _onDeleteShotOk(index: number) {
        this._shots.splice(index, 1);
        this.onChange.emit(this._shots);
    }
}
