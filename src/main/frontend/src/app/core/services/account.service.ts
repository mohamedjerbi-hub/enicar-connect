import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { UserAccount, NotificationPrefs, PrivacyPrefs } from '../models/account.model';
import { AuthService } from './auth.service';

const DEFAULT_NOTIFICATIONS: NotificationPrefs = {
    emailPosts: true,
    emailMessages: true,
    emailEvents: false,
    pushAll: true
};

const DEFAULT_PRIVACY: PrivacyPrefs = {
    profilePublic: true,
    showEmail: false,
    showPhone: false
};

@Injectable({ providedIn: 'root' })
export class AccountService {
    private authSvc = inject(AuthService);

    /** Build UserAccount from the currently authenticated user — single source of truth. */
    private buildFromAuth(): UserAccount {
        const u = this.authSvc.currentUser();
        if (!u) throw new Error('AccountService: no authenticated user');
        return {
            id: u.id,
            firstName: u.firstName ?? '',
            lastName: u.lastName ?? '',
            email: u.email ?? '',
            phone: '',
            bio: '',
            website: '',
            linkedin: '',
            github: '',
            avatarInitials: u.initials,
            avatarColor: u.avatarColor ?? '#C9A84C',
            role: (u.role as string).toLowerCase() as UserAccount['role'],
            department: (u as any).department ?? '',
            level: (u as any).level ?? '',
            notifications: { ...DEFAULT_NOTIFICATIONS },
            privacy: { ...DEFAULT_PRIVACY }
        };
    }

    private _account = new BehaviorSubject<UserAccount>(this.buildFromAuth());
    readonly account$: Observable<UserAccount> = this._account.asObservable();

    get current(): UserAccount { return this._account.value; }

    update(changes: Partial<UserAccount>): void {
        this._account.next({ ...this._account.value, ...changes });
    }

    /** Mock password change — real implementation would call the backend. */
    updatePassword(_old: string, _newPwd: string): boolean {
        return _old.length >= 1;
    }
}
