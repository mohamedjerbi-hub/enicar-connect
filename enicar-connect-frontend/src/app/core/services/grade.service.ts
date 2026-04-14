import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Grade {
  id?: number;
  studentEmail: string;
  subject: string;
  score: number;
  semester: string;
}

@Injectable({ providedIn: 'root' })
export class GradeService {
  private readonly API = 'http://localhost:8081/api/grades';
  private http = inject(HttpClient);

  getByStudent(email: string): Observable<Grade[]> {
    return this.http.get<Grade[]>(`${this.API}/student/${email}`);
  }

  addGrade(grade: Grade): Observable<Grade> {
    return this.http.post<Grade>(this.API, grade);
  }
}
