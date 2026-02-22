import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { AuthGuard } from './auth/auth.guard';

import { AppComponent } from './app.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { HeaderComponent } from './shared/header/header.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { FeedComponent } from './social/feed/feed.component';
import { GroupsComponent } from './social/groups/groups.component';
import { EventsComponent } from './social/events/events.component';
import { JobsComponent } from './professional/jobs/jobs.component';
import { FormsModule } from '@angular/forms';
import { SharedModule } from './shared/shared.module';

import { ChatModule } from './social/chat/chat.module';
import { ChatLayoutComponent } from './social/chat/chat-layout/chat-layout.component';

@NgModule({
    declarations: [
        AppComponent,
        LoginComponent,
        RegisterComponent,
        DashboardComponent,
        FeedComponent,
        GroupsComponent,
        EventsComponent,
        JobsComponent
    ],
    imports: [
        BrowserModule,
        FormsModule,
        SharedModule,
        ChatModule,
        RouterModule.forRoot([
            { path: '', redirectTo: '/login', pathMatch: 'full' },
            { path: 'login', component: LoginComponent },
            { path: 'register', component: RegisterComponent },
            { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
            { path: 'feed', component: FeedComponent, canActivate: [AuthGuard] },
            { path: 'groups', component: GroupsComponent, canActivate: [AuthGuard] },
            { path: 'messages', component: ChatLayoutComponent, canActivate: [AuthGuard] },
            { path: 'events', component: EventsComponent, canActivate: [AuthGuard] },
            { path: 'jobs', component: JobsComponent, canActivate: [AuthGuard] },
            { path: '**', redirectTo: '/dashboard' }
        ])
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule { }
