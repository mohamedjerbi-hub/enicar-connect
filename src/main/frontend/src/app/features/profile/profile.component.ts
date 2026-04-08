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

  experience = [
    { icon: 'fas fa-code', title: 'Stagiaire Développeur Web', company: 'Carthage Solutions', period: 'Juin–Août 2025', description: 'Développement Angular + Spring Boot. Pipeline CI/CD GitLab.' },
    { icon: 'fas fa-graduation-cap', title: 'Projet ENICAR Connect', company: 'ENI Carthage', period: 'Janv. 2026–Présent', description: 'Réseau social professionnel pour étudiants et enseignants.' },
  ];

  projects = [
    { name: 'eni-connect', vis: 'Public', description: 'Réseau social professionnel.', lang: 'JavaScript', langColor: '#f1e05a', stars: 18 },
    { name: 'crypto-tools', vis: 'Public', description: 'Algorithmes AES, RSA, SHA-256.', lang: 'Python', langColor: '#3572A5', stars: 9 },
    { name: 'task-api', vis: 'Privé', description: 'REST API Spring Boot + JWT.', lang: 'Java', langColor: '#b07219', stars: 4 },
    { name: 'network-scanner', vis: 'Public', description: 'Scanner réseau avec graphe topologie.', lang: 'Python', langColor: '#3572A5', stars: 7 },
  ];

  skills = [
    { name: 'Angular / TypeScript', pct: 88 },
    { name: 'Spring Boot / Java', pct: 80 },
    { name: 'Cybersécurité', pct: 70 },
    { name: 'Docker / CI-CD', pct: 65 },
    { name: 'UI / UX Design', pct: 75 },
  ];

  tags = ['Python', 'JavaScript', 'Java', 'C', 'SQL', 'Bash'];

  education = [
    { icon: 'fas fa-graduation-cap', degree: 'Génie Informatique', school: 'ENI Carthage', period: '2023–2026 (en cours)' },
    { icon: 'fas fa-school', degree: 'Baccalauréat Maths', school: 'Lycée Carthage · Bien', period: '2022–2023' },
  ];

  certifications = [
    { icon: 'fab fa-google', name: 'Google Cybersecurity', issuer: 'Google', year: '2025' },
    { icon: 'fab fa-js', name: 'JavaScript Algorithms', issuer: 'freeCodeCamp', year: '2024' },
    { icon: 'fab fa-linux', name: 'Linux Essentials', issuer: 'Cisco', year: '2024' },
  ];
}
