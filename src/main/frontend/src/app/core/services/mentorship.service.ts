import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface MentorshipDTO {
    id: number;
    partnerId: number;
    partnerName: string;
    partnerRole: string;
    partnerDepartment: string;
    objective: string;
    status: string; // PENDING, ACTIVE, COMPLETED, REJECTED
    date: string;
}

export interface MentorDTO {
    id: number;
    firstName: string;
    lastName: string;
    role: string;
    department: string;
}

@Injectable({ providedIn: 'root' })
export class MentorshipService {
    private http = inject(HttpClient);
    private API = 'http://localhost:8081/api/mentorship';

    getAvailableMentors(): Observable<MentorDTO[]> {
        return this.http.get<MentorDTO[]>(`${this.API}/available-mentors`);
    }

    getMyRequestsAsMentee(): Observable<MentorshipDTO[]> {
        return this.http.get<MentorshipDTO[]>(`${this.API}/my-requests`);
    }

    getMyMenteesAsMentor(): Observable<MentorshipDTO[]> {
        return this.http.get<MentorshipDTO[]>(`${this.API}/my-mentees`);
    }

    requestMentorship(mentorId: number, objective: string): Observable<MentorshipDTO> {
        return this.http.post<MentorshipDTO>(`${this.API}/request/${mentorId}`, { objective });
    }

    accept(requestId: number): Observable<void> {
        return this.http.post<void>(`${this.API}/accept/${requestId}`, {});
    }

    reject(requestId: number): Observable<void> {
        return this.http.post<void>(`${this.API}/reject/${requestId}`, {});
    }

    complete(requestId: number): Observable<void> {
        return this.http.post<void>(`${this.API}/complete/${requestId}`, {});
    }
}
