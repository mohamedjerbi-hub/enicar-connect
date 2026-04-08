import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { ParticlesBgComponent } from '../../shared/particles-bg/particles-bg.component';
import { ToastComponent } from '../../shared/toast/toast.component';
import { ToastService } from '../../core/services/toast.service';
import { JobService } from '../../core/services/job.service';
import { Job } from '../../core/models/job.model';

@Component({
  selector: 'app-jobs',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, ParticlesBgComponent, ToastComponent],
  templateUrl: './jobs.component.html',
  styleUrl: './jobs.component.css'
})
export class JobsComponent implements OnInit {
  private jobSvc = inject(JobService);
  private toast = inject(ToastService);

  jobs: Job[] = [];
  types = ['CDI', 'CDD', 'Stage', 'Alternance', 'Freelance'];
  locations = ['Tous', 'Tunis', 'Ariana', 'Lac 2, Tunis'];
  activeTypes: string[] = [];
  activeLocation = 'Tous';
  showModal = false;
  editingId: number | null = null;
  deleteId: number | null = null;
  form: Partial<Job> = {};
  tagsInput = '';

  ngOnInit(): void { this.jobSvc.getAll().subscribe(j => this.jobs = j); }

  filteredJobs(): Job[] {
    return this.jobs.filter(j => {
      const typeOk = this.activeTypes.length === 0 || this.activeTypes.includes(j.type);
      const locOk = this.activeLocation === 'Tous' || j.location.includes(this.activeLocation);
      return typeOk && locOk;
    });
  }

  toggleType(t: string): void {
    this.activeTypes = this.activeTypes.includes(t)
      ? this.activeTypes.filter(x => x !== t)
      : [...this.activeTypes, t];
  }

  openCreate(): void {
    this.editingId = null;
    this.form = { type: 'Stage', location: 'Tunis' };
    this.tagsInput = '';
    this.showModal = true;
  }

  openEdit(j: Job): void {
    this.editingId = j.id;
    this.form = { ...j };
    this.tagsInput = j.tags.join(', ');
    this.showModal = true;
  }

  closeModal(): void { this.showModal = false; this.editingId = null; }

  formValid(): boolean {
    return !!(this.form.title?.trim() && this.form.company?.trim() && this.form.description?.trim());
  }

  save(): void {
    if (!this.formValid()) return;
    const tags = this.tagsInput.split(',').map(t => t.trim()).filter(Boolean);
    if (this.editingId) {
      this.jobSvc.update(this.editingId, { ...this.form, tags } as Job);
      this.toast.show('fas fa-save', 'Offre modifiée !');
    } else {
      this.jobSvc.add({
        title: this.form.title!, company: this.form.company!,
        location: this.form.location || 'Tunis',
        type: (this.form.type as Job['type']) || 'Stage',
        description: this.form.description!, tags
      });
      this.toast.show('fas fa-briefcase', 'Offre publiée !');
    }
    this.closeModal();
  }

  confirmDelete(): void {
    if (this.deleteId === null) return;
    this.jobSvc.delete(this.deleteId);
    this.toast.show('fas fa-trash', 'Offre supprimée.');
    this.deleteId = null;
  }

  apply(j: Job): void {
    this.jobSvc.apply(j.id);
    this.toast.show('fas fa-paper-plane', `Candidature envoyée pour "${j.title}" !`);
  }
}
