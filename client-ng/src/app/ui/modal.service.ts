import {Injectable} from '@angular/core';

/**
 * Thx to https://jasonwatmore.com/post/2020/09/24/angular-10-custom-modal-window-dialog-box
 */
@Injectable({
    providedIn: 'root'
})
export class ModalService {
    private modals: any[] = [];

    add(modal: any): void {
        this.modals.push(modal);
    }

    remove(id: string): void {
        this.modals = this.modals.filter(x => x.id !== id);
    }

    open(id: string): void {
        const modal = this.modals.find(x => x.id === id);
        modal.openModal();
    }

    close(id: string): void {
        const modal = this.modals.find(x => x.id === id);
        modal.closeModal();
    }
}
