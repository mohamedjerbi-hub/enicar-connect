import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { ParticlesBgComponent } from '../../shared/particles-bg/particles-bg.component';
import { ToastComponent } from '../../shared/toast/toast.component';
import { ToastService } from '../../core/services/toast.service';
import { ResourceService } from '../../core/services/resource.service';
import { Resource } from '../../core/models/resource.model';

@Component({
  selector: 'app-resources',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, ParticlesBgComponent, ToastComponent],
  templateUrl: './resources.component.html',
  styleUrl: './resources.component.css'
})
export class ResourcesComponent implements OnInit {
  private resSvc = inject(ResourceService);
  toast = inject(ToastService);

  resources: Resource[] = [];
  categories: Resource['category'][] = ['Cours', 'TD / TP', 'Examens', 'PFE'];
  activeCategory = 'Tous';
  showModal = false;
  deleteId: number | null = null;
  selectedFile: File | null = null;
  form: Partial<Resource> = {};

  ngOnInit(): void { this.resSvc.getAll().subscribe(r => this.resources = r); }

  filteredResources(): Resource[] {
    return this.activeCategory === 'Tous'
      ? this.resources
      : this.resources.filter(r => r.category === this.activeCategory);
  }

  countByCategory(c: string): number { return this.resources.filter(r => r.category === c).length; }

  openUpload(): void {
    this.selectedFile = null;
    this.form = { category: 'Cours', icon: 'fas fa-file-alt', size: '0 KB', title: '' };
    this.showModal = true;
  }

  closeModal(): void { this.showModal = false; this.selectedFile = null; }

  onFileChange(e: Event): void {
    const file = (e.target as HTMLInputElement).files?.[0];
    if (!file) return;

    this.selectedFile = file; // Garde la référence pour l'envoyer

    this.form.title = this.form.title || file.name.replace(/\.[^.]+$/, '');
    this.form.size = file.size > 1048576 ? (file.size / 1048576).toFixed(1) + ' MB' : (file.size / 1024).toFixed(0) + ' KB';
    this.form.icon = file.name.endsWith('.pdf') ? 'fas fa-file-pdf'
      : file.name.endsWith('.pptx') ? 'fas fa-file-powerpoint' : 'fas fa-file-alt';
  }

  upload(): void {
    if (!this.form.title?.trim() || !this.selectedFile) return;

    // On appelle notre service en HTTP avec le File binaire
    this.resSvc.upload(
      this.selectedFile,
      this.form.title,
      this.form.category || 'Cours',
      this.form.icon || 'fas fa-file-alt',
      this.form.size || 'Inconnu'
    );

    this.toast.show('fas fa-cloud-upload-alt', 'Fichier téléchargé et publié !');
    this.closeModal();
  }

  confirmDelete(): void {
    if (this.deleteId === null) return;
    this.resSvc.delete(this.deleteId);
    this.toast.show('fas fa-trash', 'Ressource supprimée.');
    this.deleteId = null;
  }

  download(r: Resource): void {
    this.toast.show('fas fa-download', `Début du téléchargement : ${r.title}`);
    // Ouvre le lien de téléchargement fourni par Spring Boot dans un nouvel onglet
    window.open(`http://localhost:8081/api/resources/${r.id}/download`, '_blank');
  }
}
