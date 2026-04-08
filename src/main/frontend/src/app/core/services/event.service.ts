import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { AppEvent } from '../models/event.model';
import { tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class EventService {
    private readonly API = 'http://localhost:8081/api/events';
    private http = inject(HttpClient);

    private _events = new BehaviorSubject<AppEvent[]>([]);
    readonly events$: Observable<AppEvent[]> = this._events.asObservable();

    constructor() {
        this.loadEvents();
    }

    private loadEvents(): void {
        this.http.get<AppEvent[]>(this.API).subscribe({
            next: (events) => this._events.next(events),
            error: (err) => {
                console.error('Erreur lors du chargement des évènements', err);
                this._events.next([]);
            }
        });
    }

    getAll(): Observable<AppEvent[]> {
        return this.events$;
    }

    add(ev: Omit<AppEvent, 'id' | 'registeredCount' | 'registered' | 'isOwner'>): void {
        this.http.post<AppEvent>(this.API, ev).pipe(
            tap(newEv => this._events.next([...this._events.value, newEv]))
        ).subscribe();
    }

    update(id: number, changes: Partial<AppEvent>): void {
        this.http.put<AppEvent>(`${this.API}/${id}`, changes).pipe(
            tap(updatedEv => {
                const currentEvents = this._events.value.map(e => e.id === id ? updatedEv : e);
                this._events.next(currentEvents);
            })
        ).subscribe();
    }

    delete(id: number): void {
        this.http.delete(`${this.API}/${id}`).pipe(
            tap(() => {
                const filteredEvents = this._events.value.filter(e => e.id !== id);
                this._events.next(filteredEvents);
            })
        ).subscribe();
    }

    toggleRegister(id: number): void {
        this.http.post<AppEvent>(`${this.API}/${id}/toggle-register`, {}).pipe(
            tap(updatedEv => {
                const currentEvents = this._events.value.map(e => e.id === id ? updatedEv : e);
                this._events.next(currentEvents);
            })
        ).subscribe();
    }
}
