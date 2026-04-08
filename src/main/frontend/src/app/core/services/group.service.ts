import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { Group } from '../models/group.model';

@Injectable({ providedIn: 'root' })
export class GroupService {
    private readonly API = 'http://localhost:8081/api/groups';
    private http = inject(HttpClient);

    private _groups = new BehaviorSubject<Group[]>([]);
    readonly groups$ = this._groups.asObservable();

    constructor() {
        this.loadGroups();
    }

    loadGroups(): void {
        this.http.get<Group[]>(this.API).subscribe({
            next: groups => this._groups.next(groups),
            error: () => this._groups.next([])
        });
    }

    getAll(): Observable<Group[]> {
        return this.groups$;
    }

    add(group: Partial<Group>): void {
        this.http.post<Group>(this.API, group).subscribe({
            next: newGroup => {
                this._groups.next([newGroup, ...this._groups.value]);
            }
        });
    }

    update(id: number, group: Partial<Group>): void {
        this.http.put<Group>(`${this.API}/${id}`, group).subscribe({
            next: updated => {
                this._groups.next(this._groups.value.map(g => g.id === id ? updated : g));
            }
        });
    }

    delete(id: number): void {
        this.http.delete(`${this.API}/${id}`).subscribe({
            next: () => {
                this._groups.next(this._groups.value.filter(g => g.id !== id));
            }
        });
    }

    toggleJoin(id: number): void {
        this.http.post<Group>(`${this.API}/${id}/toggle-join`, {}).subscribe({
            next: updated => {
                this._groups.next(this._groups.value.map(g => g.id === id ? updated : g));
            }
        });
    }
}
