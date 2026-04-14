import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Attendance {
  id?: number;
  studentEmail: string;
  courseCode: string;
  scannedAt: string;
  present: boolean;
}

@Injectable({ providedIn: 'root' })
export class AttendanceService {
  private readonly API = 'http://localhost:8081/api/attendance';
  private http = inject(HttpClient);

  scan(studentEmail: string, courseCode: string): Observable<Attendance> {
    return this.http.post<Attendance>(`${this.API}/scan`, { studentEmail, courseCode });
  }

  getByStudent(email: string): Observable<Attendance[]> {
    return this.http.get<Attendance[]>(`${this.API}/student/${email}`);
  }
}
