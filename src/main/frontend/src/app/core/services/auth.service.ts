import { Injectable, signal, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { User, Role } from '../models/user.model';

export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    role: string;
    phone?: string;
    department?: string;
    level?: string;
}

interface AuthResponse {
    token: string;
    user: {
        id: number;
        firstName: string;
        lastName: string;
        email: string;
        phone: string;
        bio: string;
        website: string;
        linkedin: string;
        github: string;
        role: string;
        department: string;
        level: string;
        initials: string;
        fullName: string;
        avatarColor: string;
        avatarBg: string;
        permissions: string[];
    };
}

@Injectable({ providedIn: 'root' })
export class AuthService {
    private readonly API = 'http://localhost:8081/api/auth';
    private http = inject(HttpClient);
    private router = inject(Router);

    private _user = signal<User | null>(null);
    readonly currentUser = this._user.asReadonly();
    readonly isLoggedIn = () => this._user() !== null;

    constructor() {
        // Restore session from stored token
        const token = localStorage.getItem('enicar-token');
        const stored = localStorage.getItem('enicar-user');
        if (token && stored) {
            try {
                this._user.set(JSON.parse(stored));
            } catch {
                this.clearStorage();
            }
        }
    }

    async login(email: string, password: string): Promise<boolean> {
        try {
            const res = await this.http.post<AuthResponse>(`${this.API}/login`, { email, password }).toPromise();
            if (res && res.token) {
                this.handleAuthResponse(res);
                return true;
            }
            return false;
        } catch {
            return false;
        }
    }

    async register(request: RegisterRequest): Promise<{ success: boolean; error?: string }> {
        try {
            const res = await this.http.post<AuthResponse>(`${this.API}/register`, request).toPromise();
            if (res && res.token) {
                this.handleAuthResponse(res);
                return { success: true };
            }
            return { success: false, error: 'Réponse inattendue du serveur' };
        } catch (err: any) {
            const message = err.error?.error || err.error?.email || 'Erreur lors de l\'inscription';
            return { success: false, error: message };
        }
    }

    logout(): void {
        this._user.set(null);
        this.clearStorage();
        this.router.navigate(['/login']);
    }

    getToken(): string | null {
        return localStorage.getItem('enicar-token');
    }

    private handleAuthResponse(res: AuthResponse): void {
        localStorage.setItem('enicar-token', res.token);
        const user = this.mapToUser(res.user);
        localStorage.setItem('enicar-user', JSON.stringify(user));
        this._user.set(user);
    }

    private mapToUser(apiUser: AuthResponse['user']): User {
        return {
            id: apiUser.id,
            initials: apiUser.initials,
            name: apiUser.fullName,
            fullName: apiUser.fullName,
            firstName: apiUser.firstName,
            lastName: apiUser.lastName,
            email: apiUser.email,
            role: apiUser.role as Role,
            title: this.buildTitle(apiUser),
            location: 'Tunis, Tunisie',
            avatarColor: apiUser.avatarColor,
            avatarBg: apiUser.avatarBg,
            connections: 0,
            groups: 0,
            projects: 0,
            permissions: apiUser.permissions
        };
    }

    private buildTitle(apiUser: AuthResponse['user']): string {
        const parts: string[] = [];
        if (apiUser.department) parts.push(apiUser.department);
        if (apiUser.level) parts.push(apiUser.level);
        parts.push('ENI Carthage');
        return parts.join(' · ');
    }

    private clearStorage(): void {
        localStorage.removeItem('enicar-token');
        localStorage.removeItem('enicar-user');
    }
}
