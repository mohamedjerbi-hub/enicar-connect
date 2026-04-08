import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { ParticlesBgComponent } from '../../shared/particles-bg/particles-bg.component';
import { ToastComponent } from '../../shared/toast/toast.component';
import { ToastService } from '../../core/services/toast.service';
import { GroupService } from '../../core/services/group.service';
import { Group } from '../../core/models/group.model';

const ICONS = ['fas fa-code', 'fas fa-microchip', 'fas fa-shield-alt', 'fas fa-graduation-cap', 'fas fa-rocket', 'fas fa-database', 'fas fa-brain', 'fas fa-palette'];
const GRADIENTS = [
  'linear-gradient(135deg,#0A1628,#1a3060)',
  'linear-gradient(135deg,#0A1628,#C9A84C 160%)',
  'linear-gradient(135deg,#0D1F3C,#6366f1 160%)',
  'linear-gradient(135deg,#0A1628,#f43f5e 160%)',
  'linear-gradient(135deg,#0A1628,#34d399 160%)',
];

@Component({
  selector: 'app-groups',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, ParticlesBgComponent, ToastComponent],
  templateUrl: './groups.component.html',
  styleUrl: './groups.component.css'
})
export class GroupsComponent implements OnInit {
  private groupSvc = inject(GroupService);
  private router = inject(Router);
  toast = inject(ToastService);

  groups: Group[] = [];
  activeTab = 'mine';
  showModal = false;
  editingId: number | null = null;
  deleteId: number | null = null;
  icons = ICONS;
  gradients = GRADIENTS;
  form: Partial<Group> = {};

  ngOnInit(): void { this.groupSvc.getAll().subscribe(g => this.groups = g); }

  viewDetails(id: number): void {
    this.router.navigate(['/groups', id]);
  }

  filteredGroups(): Group[] {
    return this.activeTab === 'mine'
      ? this.groups.filter(g => g.joined)
      : this.groups.filter(g => !g.joined);
  }

  openCreate(): void {
    this.editingId = null;
    this.form = {
      icon: ICONS[0],
      bannerGradient: GRADIENTS[0],
      iconColor: 'var(--gold)',
      groupType: 'THEMATIC',
      privacy: 'PUBLIC'
    };
    this.showModal = true;
  }

  openEdit(g: Group): void {
    this.editingId = g.id;
    this.form = { ...g };
    this.showModal = true;
  }

  closeModal(): void { this.showModal = false; this.editingId = null; }

  save(): void {
    if (!this.form.name?.trim()) return;
    if (this.editingId) {
      this.groupSvc.update(this.editingId, this.form as Group);
      this.toast.show('fas fa-save', 'Groupe modifié !');
    } else {
      this.groupSvc.add({
        name: this.form.name!, description: this.form.description || '',
        icon: this.form.icon!, iconColor: this.form.iconColor || 'var(--gold)',
        bannerGradient: this.form.bannerGradient!
      });
      this.toast.show('fas fa-users', 'Groupe créé !');
    }
    this.closeModal();
  }

  confirmDelete(): void {
    if (this.deleteId === null) return;
    this.groupSvc.delete(this.deleteId);
    this.toast.show('fas fa-trash', 'Groupe supprimé.');
    this.deleteId = null;
  }

  toggleJoin(g: Group): void {
    this.groupSvc.toggleJoin(g.id);
    this.toast.show(
      g.joined ? 'fas fa-user-minus' : 'fas fa-user-check',
      g.joined ? 'Groupe quitté.' : 'Vous avez rejoint le groupe !'
    );
  }
}
