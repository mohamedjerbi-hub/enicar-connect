import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Observable } from 'rxjs';

import { PostService } from '../../core/services/post.service';
import { ToastService } from '../../core/services/toast.service';
import { AuthService } from '../../core/services/auth.service';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { RoleBadgeComponent } from '../../shared/role-badge/role-badge.component';
import { ParticlesBgComponent } from '../../shared/particles-bg/particles-bg.component';
import { ToastComponent } from '../../shared/toast/toast.component';
import { Post } from '../../core/models/post.model';

@Component({
  selector: 'app-social-feed',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, NavbarComponent, RoleBadgeComponent, ParticlesBgComponent, ToastComponent],
  templateUrl: './social-feed.component.html',
  styleUrl: './social-feed.component.css'
})
export class SocialFeedComponent {
  readonly posts$: Observable<Post[]>;
  composerText = '';
  commentTexts: Record<number, string> = {};
  deletePostId: number | null = null;
  editingPostId: number | null = null;
  editBodyText = '';
  reportPostId: number | null = null;
  reportReason = '';

  trending = [
    { tag: 'AngularDev', count: 42 },
    { tag: 'Cybersécurité', count: 38 },
    { tag: 'ENICAR2026', count: 31 },
    { tag: 'HackathonENI', count: 27 },
    { tag: 'PFE2026', count: 19 },
  ];

  suggestions = [
    { initials: 'KM', name: 'Karim Mejri', desc: 'Alumni · Google Cloud', bg: '#10b981' },
    { initials: 'LB', name: 'Leila Ben Salah', desc: 'INFO2 — Groupe A', bg: '#6366f1' },
    { initials: 'AM', name: 'Ahmed Mansour', desc: 'INFO2 — Groupe B', bg: '#f43f5e' },
  ];

  constructor(
    public readonly postSvc: PostService,
    public readonly toast: ToastService,
    public readonly auth: AuthService
  ) {
    this.posts$ = postSvc.posts$;
  }

  postText(): void {
    if (!this.composerText.trim()) return;
    this.postSvc.createPost(this.composerText.trim());
    this.composerText = '';
    this.toast.show('fas fa-check-circle', 'Publication envoyée !');
  }

  addComment(postId: number): void {
    const text = this.commentTexts[postId]?.trim();
    if (!text) return;
    this.postSvc.addComment(postId, text);
    this.commentTexts[postId] = '';
  }

  startEdit(post: Post): void {
    this.editingPostId = post.id;
    this.editBodyText = post.body;
  }

  saveEdit(postId: number): void {
    if (!this.editBodyText.trim()) return;
    this.postSvc.updatePost(postId, this.editBodyText.trim());
    this.editingPostId = null;
    this.toast.show('fas fa-edit', 'Publication modifiée !');
  }

  confirmDeletePost(): void {
    if (this.deletePostId === null) return;
    this.postSvc.deletePost(this.deletePostId);
    this.deletePostId = null;
    this.toast.show('fas fa-trash', 'Publication supprimée.');
  }

  formatBody(body: string): string {
    return body
      .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
      .replace(/#(\w+)/g, '<span style="color:var(--gold);font-weight:600;cursor:pointer">#$1</span>')
      .replace(/\n/g, '<br>');
  }

  submitReport(): void {
    if (this.reportPostId === null || !this.reportReason.trim()) return;
    this.postSvc.reportPost(this.reportPostId, this.reportReason.trim()).subscribe({
      next: () => {
        this.toast.show('fas fa-flag', 'Signalement envoyé aux modérateurs.');
        this.reportPostId = null;
        this.reportReason = '';
      }
    });
  }
}
