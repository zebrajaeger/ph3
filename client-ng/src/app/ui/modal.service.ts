import { Injectable } from '@angular/core';
import { ModalComponent } from './modal.component';

/**
 * Thx to https://jasonwatmore.com/post/2020/09/24/angular-10-custom-modal-window-dialog-box
 */
@Injectable({
    providedIn: 'root'
})
export class ModalService {
    private modals: ModalComponent[] = [];

    add(modal: ModalComponent): void {
        this.modals.push(modal);
    }

    remove(id: string): void {
        this.modals = this.modals.filter(x => x.id !== id);
    }

    open(id: string): void {
        const modal = this.modals.find(x => x.id === id);
        if (modal) {
            modal.openModal();
        } else {
            console.log('unknown modal id to open:', id)
        }
    }

    close(id: string): void {
        const modal = this.modals.find(x => x.id === id);
        if (modal) {
            modal.closeModal();
        } else {
            console.log('unknown modal id to close:', id)
        }
    }
}
