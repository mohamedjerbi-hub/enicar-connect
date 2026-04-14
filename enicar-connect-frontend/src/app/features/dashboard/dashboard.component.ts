import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { GradeService, Grade } from '../../core/services/grade.service';
import { AttendanceService, Attendance } from '../../core/services/attendance.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  private auth = inject(AuthService);
  private gradeService = inject(GradeService);
  private attendanceService = inject(AttendanceService);

  studentEmail = this.auth.currentUser()?.email ?? '';

  grades = signal<Grade[]>([]);
  attendances = signal<Attendance[]>([]);
  loadingGrades = signal(true);
  loadingAttendances = signal(true);
  errorGrades = signal('');
  errorAttendances = signal('');

  average = computed(() => {
    const g = this.grades();
    return g.length > 0 ? g.reduce((sum, x) => sum + x.score, 0) / g.length : 0;
  });

  presentCount = computed(() => this.attendances().filter(a => a.present).length);
  absentCount  = computed(() => this.attendances().filter(a => !a.present).length);

  ngOnInit(): void {
    this.loadGrades();
    this.loadAttendances();
  }

  loadGrades(): void {
    this.gradeService.getByStudent(this.studentEmail).subscribe({
      next: (data) => {
        this.grades.set(data);
        this.loadingGrades.set(false);
      },
      error: () => {
        this.errorGrades.set('Erreur lors du chargement des notes.');
        this.loadingGrades.set(false);
      }
    });
  }

  loadAttendances(): void {
    this.attendanceService.getByStudent(this.studentEmail).subscribe({
      next: (data) => {
        this.attendances.set(data);
        this.loadingAttendances.set(false);
      },
      error: () => {
        this.errorAttendances.set('Erreur lors du chargement des présences.');
        this.loadingAttendances.set(false);
      }
    });
  }
}
