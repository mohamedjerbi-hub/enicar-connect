export type Role = 'student' | 'teacher' | 'admin_staff' | 'direction' | 'alumni';

export interface User {
    id: number;
    initials: string;
    name: string;
    fullName?: string;
    firstName?: string;
    lastName?: string;
    email?: string;
    role: Role;
    title: string;
    location: string;
    avatarColor: string;
    avatarBg: string;
    connections: number;
    groups: number;
    projects: number;
    permissions?: string[];
}

export const ROLE_META: Record<Role, { label: string; icon: string; cls: string }> = {
    student: { label: 'Étudiant', icon: 'fa-graduation-cap', cls: 'student' },
    teacher: { label: 'Enseignant', icon: 'fa-chalkboard-teacher', cls: 'prof' },
    admin_staff: { label: 'Administration', icon: 'fa-university', cls: 'admin' },
    direction: { label: 'Direction', icon: 'fa-building', cls: 'direction' },
    alumni: { label: 'Ancien Étudiant', icon: 'fa-user-tie', cls: 'alumni' },
};

