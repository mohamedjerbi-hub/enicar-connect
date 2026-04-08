import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { ParticlesBgComponent } from '../../shared/particles-bg/particles-bg.component';
import { ToastComponent } from '../../shared/toast/toast.component';
import { ToastService } from '../../core/services/toast.service';
import { EventService } from '../../core/services/event.service';
import { AppEvent } from '../../core/models/event.model';

type Category = AppEvent['category'];
const CATEGORIES: Category[] = ['Conférence', 'Hackathon', 'Forum', 'Atelier', 'Soutenance', 'Autre'];
const COLORS = [
  'linear-gradient(135deg,#0A1628,#1a3060)',
  'linear-gradient(135deg,#0A1628,#C9A84C 160%)',
  'linear-gradient(135deg,#0D1F3C,#6366f1 160%)',
  'linear-gradient(135deg,#0A1628,#f43f5e 160%)',
  'linear-gradient(135deg,#0A1628,#34d399 160%)',
];

@Component({
  selector: 'app-events',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, ParticlesBgComponent, ToastComponent],
  templateUrl: './events.component.html',
  styleUrl: './events.component.css'
})
export class EventsComponent implements OnInit {
  private eventSvc = inject(EventService);
  private toast = inject(ToastService);

  events: AppEvent[] = [];
  categories = CATEGORIES;
  colorOptions = COLORS;
  showModal = false;
  editingId: number | null = null;
  deleteId: number | null = null;
  form: Partial<AppEvent> = {};

  ngOnInit(): void { this.eventSvc.getAll().subscribe(e => this.events = e); }

  registeredCount(): number { return this.events.filter(e => e.registered).length; }

  openCreate(): void {
    this.editingId = null;
    this.form = { category: 'Conférence', color: COLORS[0], organizer: 'Mohamed Jerbi' };
    this.showModal = true;
  }

  openEdit(ev: AppEvent): void {
    this.editingId = ev.id;
    this.form = { ...ev };
    this.showModal = true;
  }

  closeModal(): void { this.showModal = false; this.editingId = null; }

  formValid(): boolean {
    return !!(this.form.title?.trim() && this.form.date && this.form.time && this.form.location?.trim());
  }

  saveEvent(): void {
    if (!this.formValid()) return;
    if (this.editingId) {
      this.eventSvc.update(this.editingId, this.form as AppEvent);
      this.toast.show('fas fa-save', 'Évènement modifié !');
    } else {
      this.eventSvc.add(this.form as Omit<AppEvent, 'id' | 'registeredCount' | 'registered' | 'isOwner'>);
      this.toast.show('fas fa-calendar-plus', 'Évènement créé !');
    }
    this.closeModal();
  }

  promptDelete(id: number): void { this.deleteId = id; }

  confirmDelete(): void {
    if (this.deleteId === null) return;
    this.eventSvc.delete(this.deleteId);
    this.toast.show('fas fa-trash', 'Évènement supprimé.');
    this.deleteId = null;
  }

  toggle(ev: AppEvent): void {
    this.eventSvc.toggleRegister(ev.id);
    this.toast.show(
      ev.registered ? 'fas fa-calendar-minus' : 'fas fa-calendar-check',
      ev.registered ? 'Inscription annulée.' : 'Inscription confirmée !'
    );
  }

  formatDate(d: string): string {
    if (!d) return '';
    return new Date(d).toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' });
  }
}
