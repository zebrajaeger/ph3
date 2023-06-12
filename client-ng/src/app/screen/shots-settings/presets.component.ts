import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ShotsPresets} from "../../../data/camera";

@Component({
    selector: 'presets',
    templateUrl: './presets.component.html',
    styleUrls: ['./presets.component.scss']
})
export class PresetsComponent {

    @Input() public presets!: ShotsPresets;
    @Output() public onEdit = new EventEmitter<string>();
    @Output() public onLoad = new EventEmitter<string>();
    @Output() public onDelete = new EventEmitter<string>();

    @Input() editable = true;

    _onAdd() {

    }

    _onDelete(name: string) {
        this.onDelete.emit(name)
    }

    _onEdit(name: string) {
        this.onEdit.emit(name)
    }

    _onLoad(name: string) {
        this.onLoad.emit(name)
    }
}
