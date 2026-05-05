import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { ParticlesBgComponent } from '../../shared/particles-bg/particles-bg.component';
import { ToastComponent } from '../../shared/toast/toast.component';
import { ToastService } from '../../core/services/toast.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, RouterLink, NavbarComponent, ParticlesBgComponent, ToastComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
  toast = inject(ToastService);
  auth = inject(AuthService);

  get user() { return this.auth.currentUser(); }

  get experience() {
    return this.user?.experiences ?? [];
  }

  projects: any[] = []; // Could stay empty or be linked to a new service later

  get skills() {
    const userSkills = this.user?.skills ?? [];
    return userSkills.map((name, index) => ({ name, pct: Math.max(50, 90 - index * 7) }));
  }

  get tags() {
    return this.user?.skills ?? [];
  }

  get education() {
    return this.user?.educations ?? [];
  }

  certifications: any[] = []; // Linked to an actual certification entity if exists
}
