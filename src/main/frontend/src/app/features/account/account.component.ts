import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { ParticlesBgComponent } from '../../shared/particles-bg/particles-bg.component';
import { ToastComponent } from '../../shared/toast/toast.component';
import { ToastService } from '../../core/services/toast.service';
import { AccountService } from '../../core/services/account.service';
import { UserAccount } from '../../core/models/account.model';

@Component({
  selector: 'app-account',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, ParticlesBgComponent, ToastComponent],
  templateUrl: './account.component.html',
  styleUrl: './account.component.css'
})
export class AccountComponent {
  private accountSvc = inject(AccountService);
  private toast = inject(ToastService);

  tab = 'info';
  showDeleteConfirm = false;
  deleteConfirmText = '';
  showPwd = false;

  get user() { return this.accountSvc.current; }
  editUser: UserAccount = {
    ...this.accountSvc.current,
    notifications: { ...this.accountSvc.current.notifications },
    privacy: { ...this.accountSvc.current.privacy }
  };

  pwd = { old: '', new: '', confirm: '' };

  sessions = [
    { icon: 'fas fa-laptop', device: 'Chrome · Windows 11', location: 'Tunis, TN', time: 'Maintenant', current: true },
    { icon: 'fas fa-mobile-alt', device: 'Safari · iPhone 15', location: 'Tunis, TN', time: 'Hier, 20:14', current: false },
    { icon: 'fas fa-desktop', device: 'Firefox · Ubuntu', location: 'Ariana, TN', time: 'Il y a 3 jours', current: false },
  ];

  notifSettings: Array<{ key: keyof UserAccount['notifications'], label: string, desc: string }> = [
    { key: 'emailPosts', label: 'Nouvelles publications', desc: 'Recevoir un email quand quelqu\'un publie dans vos groupes' },
    { key: 'emailMessages', label: 'Nouveaux messages', desc: 'Notification email pour chaque message reçu' },
    { key: 'emailEvents', label: 'Évènements à venir', desc: 'Rappels email pour les évènements auxquels vous êtes inscrit' },
    { key: 'pushAll', label: 'Toutes les notifications push', desc: 'Activer toutes les notifications dans l\'application' },
  ];

  privacySettings: Array<{ key: keyof UserAccount['privacy'], label: string, desc: string }> = [
    { key: 'profilePublic', label: 'Profil public', desc: 'Votre profil est visible par tous les membres' },
    { key: 'showEmail', label: 'Afficher l\'email', desc: 'Votre adresse email apparaît sur votre profil public' },
    { key: 'showPhone', label: 'Afficher le téléphone', desc: 'Votre numéro de téléphone apparaît sur votre profil public' },
  ];

  roleLabel(r: string): string {
    return ({ student: 'Étudiant', teacher: 'Enseignant', admin_staff: 'Administration', direction: 'Direction', alumni: 'Alumni' } as Record<string, string>)[r] ?? r;
  }

  saveInfo(): void {
    this.accountSvc.update({ ...this.editUser });
    this.toast.show('fas fa-check-circle', 'Informations mises à jour !');
  }

  saveNotifications(): void {
    this.accountSvc.update({ notifications: { ...this.editUser.notifications } });
    this.toast.show('fas fa-bell', 'Préférences de notification enregistrées !');
  }

  savePrivacy(): void {
    this.accountSvc.update({ privacy: { ...this.editUser.privacy } });
    this.toast.show('fas fa-shield-alt', 'Paramètres de confidentialité enregistrés !');
  }

  pwdValid(): boolean { return this.pwd.old.length > 0 && this.pwd.new.length >= 8 && this.pwd.new === this.pwd.confirm; }

  pwdStrength(): number {
    const p = this.pwd.new;
    let s = 0;
    if (p.length >= 8) s += 25;
    if (/[A-Z]/.test(p)) s += 25;
    if (/[0-9]/.test(p)) s += 25;
    if (/[^A-Za-z0-9]/.test(p)) s += 25;
    return s;
  }

  pwdLabel(): string {
    const s = this.pwdStrength();
    return s < 40 ? 'Faible' : s < 75 ? 'Moyen' : 'Fort';
  }

  changePassword(): void {
    const ok = this.accountSvc.updatePassword(this.pwd.old, this.pwd.new);
    if (ok) {
      this.toast.show('fas fa-key', 'Mot de passe modifié avec succès !');
      this.pwd = { old: '', new: '', confirm: '' };
    } else {
      this.toast.show('fas fa-exclamation-circle', 'Mot de passe actuel incorrect.');
    }
  }

  revokeSession(s: { device: string }): void {
    this.sessions = this.sessions.filter(x => x !== s);
    this.toast.show('fas fa-sign-out-alt', `Session "${s.device}" révoquée.`);
  }

  deleteAccount(): void {
    if (this.deleteConfirmText !== 'SUPPRIMER') return;
    this.toast.show('fas fa-trash', 'Compte supprimé (simulation).');
    this.showDeleteConfirm = false;
    this.deleteConfirmText = '';
  }
}
