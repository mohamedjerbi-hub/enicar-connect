import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { Job } from '../models/job.model';
import { tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

interface MatchingResult {
    jobOfferId: number;
    matchedSkills: string[];
    compatibilityScore: number;
}

@Injectable({ providedIn: 'root' })
export class JobService {
    private readonly API = `${environment.apiUrl}/jobs`;
    private http = inject(HttpClient);

    private _jobs = new BehaviorSubject<Job[]>([]);
    readonly jobs$: Observable<Job[]> = this._jobs.asObservable();

    constructor() {
        this.loadJobs();
    }

    private loadJobs(): void {
        this.http.get<Job[]>(this.API).subscribe({
            next: (jobs) => {
                this._jobs.next(jobs);
                this.refreshMatchingScores();
            },
            error: (err) => {
                console.error('Erreur lors du chargement des offres', err);
                this._jobs.next([]);
            }
        });
    }

    getAll(): Observable<Job[]> {
        return this.jobs$;
    }

    add(job: Omit<Job, 'id' | 'applied' | 'posted' | 'isOwner'>): void {
        this.http.post<Job>(this.API, {
            ...job,
            requiredSkills: job.requiredSkills ?? job.tags
        }).pipe(
            tap(newJob => {
                this._jobs.next([newJob, ...this._jobs.value]);
                this.refreshMatchingScores();
            })
        ).subscribe();
    }

    update(id: number, changes: Partial<Job>): void {
        this.http.put<Job>(`${this.API}/${id}`, {
            ...changes,
            requiredSkills: changes.requiredSkills ?? changes.tags
        }).pipe(
            tap(updatedJob => {
                const currentJobs = this._jobs.value.map(j => j.id === id ? updatedJob : j);
                this._jobs.next(currentJobs);
                this.refreshMatchingScores();
            })
        ).subscribe();
    }

    delete(id: number): void {
        this.http.delete(`${this.API}/${id}`).pipe(
            tap(() => {
                const filteredJobs = this._jobs.value.filter(j => j.id !== id);
                this._jobs.next(filteredJobs);
            })
        ).subscribe();
    }

    apply(id: number): void {
        this.http.post(`${this.API}/${id}/apply`, {}).pipe(
            tap(() => {
                const currentJobs = this._jobs.value.map(j => j.id === id ? { ...j, applied: true } : j);
                this._jobs.next(currentJobs);
            })
        ).subscribe();
    }

    private refreshMatchingScores(): void {
        this.http.get<MatchingResult[]>('http://localhost:8081/api/matching/jobs').subscribe({
            next: matches => {
                const byJobId = new Map(matches.map(m => [m.jobOfferId, m]));
                const merged = this._jobs.value.map(job => {
                    const match = byJobId.get(job.id);
                    if (!match) return { ...job, compatibilityScore: undefined, matchedSkills: [] };
                    return {
                        ...job,
                        compatibilityScore: Math.round(match.compatibilityScore),
                        matchedSkills: match.matchedSkills ?? []
                    };
                });
                this._jobs.next(merged);
            },
            error: () => {
                // Matching is an enhancement; keep jobs visible even if endpoint fails.
            }
        });
    }
}
