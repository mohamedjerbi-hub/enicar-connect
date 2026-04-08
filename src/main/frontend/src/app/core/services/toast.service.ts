import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export interface Toast {
    id: number;
    icon: string;
    message: string;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
    private _counter = 0;
    toast$ = new Subject<Toast>();

    show(icon: string, message: string): void {
        this.toast$.next({ id: ++this._counter, icon, message });
    }
}
