import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { Post, CommentData } from '../models/post.model';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class PostService {
    private readonly API = 'http://localhost:8081/api';
    private http = inject(HttpClient);
    private auth = inject(AuthService);

    private _posts = new BehaviorSubject<Post[]>([]);
    readonly posts$: Observable<Post[]> = this._posts.asObservable();

    constructor() {
        this.loadPosts();
    }

    // ─── Load all posts from API ─────────────────────────

    loadPosts(): void {
        this.http.get<Post[]>(`${this.API}/posts`).subscribe({
            next: posts => this._posts.next(posts.map(p => ({ ...p, commentsOpen: false }))),
            error: () => this._posts.next([]) // Fallback to empty if API unreachable
        });
    }

    // ─── Create ──────────────────────────────────────────

    createPost(body: string, visibility = 'PUBLIC'): void {
        this.http.post<Post>(`${this.API}/posts`, { body, visibility }).subscribe({
            next: post => {
                this._posts.next([{ ...post, commentsOpen: false }, ...this._posts.value]);
            }
        });
    }

    // ─── Update ──────────────────────────────────────────

    updatePost(id: number, body: string): void {
        this.http.put<Post>(`${this.API}/posts/${id}`, { body, visibility: 'PUBLIC' }).subscribe({
            next: updated => {
                this._posts.next(
                    this._posts.value.map(p => p.id === id ? { ...updated, commentsOpen: p.commentsOpen } : p)
                );
            }
        });
    }

    // ─── Delete ──────────────────────────────────────────

    deletePost(id: number): void {
        this.http.delete(`${this.API}/posts/${id}`).subscribe({
            next: () => {
                this._posts.next(this._posts.value.filter(p => p.id !== id));
            }
        });
    }

    // ─── Like / Unlike ───────────────────────────────────

    toggleLike(postId: number): void {
        this.http.post<Post>(`${this.API}/posts/${postId}/like`, {}).subscribe({
            next: updated => {
                this._posts.next(
                    this._posts.value.map(p => p.id === postId ? { ...updated, commentsOpen: p.commentsOpen } : p)
                );
            }
        });
    }

    // ─── Comments ────────────────────────────────────────

    toggleComments(postId: number): void {
        this._posts.next(
            this._posts.value.map(p =>
                p.id === postId ? { ...p, commentsOpen: !p.commentsOpen } : p
            )
        );
    }

    addComment(postId: number, text: string): void {
        this.http.post<CommentData>(`${this.API}/posts/${postId}/comments`, { text }).subscribe({
            next: comment => {
                this._posts.next(
                    this._posts.value.map(p => {
                        if (p.id !== postId) return p;
                        return {
                            ...p,
                            comments: [...p.comments, comment],
                            commentsCount: p.commentsCount + 1
                        };
                    })
                );
            }
        });
    }

    deleteComment(postId: number, commentId: number): void {
        this.http.delete(`${this.API}/comments/${commentId}`).subscribe({
            next: () => {
                this._posts.next(
                    this._posts.value.map(p => {
                        if (p.id !== postId) return p;
                        return {
                            ...p,
                            comments: p.comments.filter(c => c.id !== commentId),
                            commentsCount: p.commentsCount - 1
                        };
                    })
                );
            }
        });
    }

    // ─── Reporting ───────────────────────────────────────

    reportPost(postId: number, reason: string): Observable<any> {
        return this.http.post(`${this.API}/posts/${postId}/report`, { reason });
    }

    // ─── Share (copy link) ───────────────────────────────

    sharePost(postId: number): void {
        navigator.clipboard?.writeText(`https://enicar.connect/post/${postId}`).catch(() => { });
    }
}
