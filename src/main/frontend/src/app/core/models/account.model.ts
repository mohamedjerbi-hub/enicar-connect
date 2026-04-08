export interface UserAccount {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
    bio: string;
    website: string;
    linkedin: string;
    github: string;
    avatarInitials: string;
    avatarColor: string;
    role: 'student' | 'teacher' | 'admin_staff' | 'direction' | 'alumni';
    department: string;
    level: string;
    notifications: NotificationPrefs;
    privacy: PrivacyPrefs;
}

export interface NotificationPrefs {
    emailPosts: boolean;
    emailMessages: boolean;
    emailEvents: boolean;
    pushAll: boolean;
}

export interface PrivacyPrefs {
    profilePublic: boolean;
    showEmail: boolean;
    showPhone: boolean;
}
