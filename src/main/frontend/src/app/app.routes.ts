import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    {
        path: 'login',
        loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
    },
    {
        path: 'register',
        loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent)
    },
    {
        path: 'feed',
        loadComponent: () => import('./features/social-feed/social-feed.component').then(m => m.SocialFeedComponent),
        canActivate: [authGuard]
    },
    {
        path: 'messaging',
        loadComponent: () => import('./features/messaging/messaging.component').then(m => m.MessagingComponent),
        canActivate: [authGuard]
    },
    {
        path: 'profile',
        loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent),
        canActivate: [authGuard]
    },
    {
        path: 'jobs',
        loadComponent: () => import('./features/jobs/jobs.component').then(m => m.JobsComponent),
        canActivate: [authGuard]
    },
    {
        path: 'groups',
        loadComponent: () => import('./features/groups/groups.component').then(m => m.GroupsComponent),
        canActivate: [authGuard]
    },
    {
        path: 'groups/:id',
        loadComponent: () => import('./features/groups/group-detail/group-detail.component').then(c => c.GroupDetailComponent),
        canActivate: [authGuard]
    },
    {
        path: 'resources',
        loadComponent: () => import('./features/resources/resources.component').then(m => m.ResourcesComponent),
        canActivate: [authGuard]
    },
    {
        path: 'events',
        loadComponent: () => import('./features/events/events.component').then(m => m.EventsComponent),
        canActivate: [authGuard]
    },
    {
        path: 'account',
        loadComponent: () => import('./features/account/account.component').then(m => m.AccountComponent),
        canActivate: [authGuard]
    },
    {
        path: 'mentorship',
        loadComponent: () => import('./features/mentorship/mentorship.component').then(m => m.MentorshipComponent),
        canActivate: [authGuard]
    },
    { path: '**', redirectTo: 'login' }
];
