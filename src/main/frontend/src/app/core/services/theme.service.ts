import { Injectable, signal } from '@angular/core';

export type Theme = 'dark' | 'light';

@Injectable({ providedIn: 'root' })
export class ThemeService {
    private _theme = signal<Theme>(
        (localStorage.getItem('enicar-theme') as Theme) || 'dark'
    );

    readonly theme = this._theme.asReadonly();

    constructor() {
        this.apply(this._theme());
    }

    toggle(): void {
        const next: Theme = this._theme() === 'dark' ? 'light' : 'dark';
        this._theme.set(next);
        localStorage.setItem('enicar-theme', next);
        this.apply(next);
    }

    private apply(theme: Theme): void {
        document.documentElement.setAttribute('data-theme', theme);
    }
}
