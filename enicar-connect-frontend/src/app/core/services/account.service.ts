import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, firstValueFrom } from 'rxjs';
import { UserAccount } from '../models/account.model';
import { AuthService } from './auth.service';

const DEFAULT_ACCOUNT: UserAccount = {
    id: 1,
    firstName: 'Mohamed',
    lastName: 'Jerbi',
    email: 'mohamed.jerbi@enicar.u-carthage.tn',
    phone: '+216 99 123 456',
    bio: 'Étudiant en 2ème année INFO à ENI Carthage. Passionné de développement web et de cybersécurité. Toujours à la recherche de nouveaux défis.',
    website: 'https://jerbi.dev',
    linkedin: 'https://linkedin.com/in/mjerbi',
    github: 'https://github.com/mjerbi',
    avatarInitials: 'MJ',
    avatarColor: 'rgba(99,102,241,.15)',
    role: 'student',
    department: 'Informatique',
    level: '2ème année',
    skills: ['Java', 'Spring Boot', 'Angular'],
    notifications: { emailPosts: true, emailMessages: true, emailEvents: false, pushAll: true },
    privacy: { profilePublic: true, showEmail: false, showPhone: false }
};

@Injectable({ providedIn: 'root' })
export class AccountService {
    private readonly API = 'http://localhost:8081/api/users';
    private http = inject(HttpClient);
    private auth = inject(AuthService);
    private _account = new BehaviorSubject<UserAccount>(DEFAULT_ACCOUNT);
    readonly account$: Observable<UserAccount> = this._account.asObservable();

    constructor() {
        this.refreshFromCurrentUser();
    }

    get current(): UserAccount {
        return this._account.value;
    }

    refreshFromCurrentUser(): void {
        const user = this.auth.currentUser();
        if (!user?.id) {
            return;
        }
        this.http.get<any>(`${this.API}/${user.id}`).subscribe({
            next: (dto) => this._account.next(this.mapToAccount(dto)),
            error: () => this._account.next(this.mapFromAuthFallback())
        });
    }

    async update(changes: Partial<UserAccount>): Promise<boolean> {
        const current = this._account.value;
        const merged = { ...current, ...changes };
        const user = this.auth.currentUser();
        if (!user?.id) {
            this._account.next(merged);
            return false;
        }

        const payload: any = {
            firstName: merged.firstName,
            lastName: merged.lastName,
            phone: merged.phone,
            bio: merged.bio,
            website: merged.website,
            linkedin: merged.linkedin,
            github: merged.github,
            department: merged.department,
            level: merged.level,
            skills: merged.skills
        };

        try {
            const updated = await firstValueFrom(this.http.put<any>(`${this.API}/${user.id}`, payload));
            this._account.next(this.mapToAccount(updated));
            return true;
        } catch {
            return false;
        }
    }

    updatePassword(_old: string, _newPwd: string): boolean {
        // Mock: always succeeds as long as old password is not empty
        return _old.length >= 1;
    }

    private mapFromAuthFallback(): UserAccount {
        const user = this.auth.currentUser();
        if (!user) {
            return DEFAULT_ACCOUNT;
        }
        return {
            ...DEFAULT_ACCOUNT,
            id: user.id,
            firstName: user.firstName || '',
            lastName: user.lastName || '',
            email: user.email || '',
            avatarInitials: user.initials,
            avatarColor: user.avatarBg,
            role: user.role,
            department: this.extractDepartment(user.title),
            level: this.extractLevel(user.title),
            skills: user.skills ?? []
        };
    }

    private mapToAccount(dto: any): UserAccount {
        return {
            id: dto.id,
            firstName: dto.firstName || '',
            lastName: dto.lastName || '',
            email: dto.email || '',
            phone: dto.phone || '',
            bio: dto.bio || '',
            website: dto.website || '',
            linkedin: dto.linkedin || '',
            github: dto.github || '',
            avatarInitials: dto.initials || '',
            avatarColor: dto.avatarBg || DEFAULT_ACCOUNT.avatarColor,
            role: (dto.role || 'student') as UserAccount['role'],
            department: dto.department || '',
            level: dto.level || '',
            skills: dto.skills ?? [],
            notifications: { ...this._account.value.notifications },
            privacy: { ...this._account.value.privacy }
        };
    }

    private extractDepartment(title?: string): string {
        if (!title) return '';
        return title.split('·')[0]?.trim() || '';
    }

    private extractLevel(title?: string): string {
        if (!title) return '';
        return title.split('·')[1]?.trim() || '';
    }
}
