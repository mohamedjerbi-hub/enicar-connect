import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { Resource } from '../models/resource.model';
import { tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class ResourceService {
    private readonly API = 'http://localhost:8081/api/resources';
    private http = inject(HttpClient);

    private _resources = new BehaviorSubject<Resource[]>([]);
    readonly resources$: Observable<Resource[]> = this._resources.asObservable();

    constructor() {
        this.loadResources();
    }

    private loadResources(): void {
        this.http.get<Resource[]>(this.API).subscribe({
            next: (resources) => this._resources.next(resources),
            error: (err) => {
                console.error('Erreur de chargement des ressources', err);
                this._resources.next([]);
            }
        });
    }

    getAll(): Observable<Resource[]> {
        return this.resources$;
    }

    upload(file: File, title: string, category: string, icon: string, size: string): void {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('title', title);
        formData.append('category', category);
        formData.append('icon', icon);
        formData.append('size', size);

        this.http.post<Resource>(this.API, formData).pipe(
            tap(newRes => this._resources.next([newRes, ...this._resources.value]))
        ).subscribe();
    }

    delete(id: number): void {
        this.http.delete(`${this.API}/${id}`).pipe(
            tap(() => {
                const filteredResources = this._resources.value.filter(r => r.id !== id);
                this._resources.next(filteredResources);
            })
        ).subscribe();
    }
}
