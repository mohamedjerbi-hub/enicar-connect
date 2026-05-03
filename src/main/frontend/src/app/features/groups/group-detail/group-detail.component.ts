import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { GroupService } from '../../../core/services/group.service';
import { Group } from '../../../core/models/group.model';
import { PostService } from '../../../core/services/post.service';
import { Post } from '../../../core/models/post.model';
import { GroupMessageService } from '../../../core/services/group-message.service';
import { GroupMessage } from '../../../core/models/group-message.model';
import { NavbarComponent } from '../../../shared/navbar/navbar.component';
import { ParticlesBgComponent } from '../../../shared/particles-bg/particles-bg.component';
import { RoleBadgeComponent } from '../../../shared/role-badge/role-badge.component';

@Component({
    selector: 'app-group-detail',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink, NavbarComponent, ParticlesBgComponent, RoleBadgeComponent],
    templateUrl: './group-detail.component.html',
    styleUrl: './group-detail.component.css'
})
export class GroupDetailComponent implements OnInit {
    private route = inject(ActivatedRoute);
    private groupSvc = inject(GroupService);
    private postSvc = inject(PostService);
    private groupMsgSvc = inject(GroupMessageService);

    group: Group | null = null;
    posts: Post[] = [];
    activeTab = 'feed'; // feed, messages, members, resources, events
    composerText = '';
    messages: GroupMessage[] = [];
    messageText = '';
    loadingMessages = false;

    ngOnInit(): void {
        const id = this.route.snapshot.params['id'];
        this.groupSvc.getAll().subscribe(groups => {
            this.group = groups.find(g => g.id === +id) || null;
        });
        this.postSvc.posts$.subscribe(posts => {
            this.posts = posts.filter(p => p.groupId === +id);
        });
        this.postSvc.loadPosts();
        this.loadMessages();
    }

    setTab(tab: string): void {
        this.activeTab = tab;
        if (tab === 'messages') {
            this.loadMessages();
        }
    }

    postText(): void {
        if (!this.composerText.trim() || !this.group) return;
        this.postSvc.createPost(this.composerText.trim(), 'GROUP', this.group.id);
        this.composerText = '';
    }

    loadMessages(): void {
        const groupId = Number(this.route.snapshot.params['id']);
        if (!groupId) return;

        this.loadingMessages = true;
        this.groupMsgSvc.getMessages(groupId).subscribe({
            next: (msgs) => {
                this.messages = msgs;
                this.loadingMessages = false;
                this.scrollMessagesToBottom();
            },
            error: () => {
                this.messages = [];
                this.loadingMessages = false;
            }
        });
    }

    sendMessage(): void {
        const groupId = Number(this.route.snapshot.params['id']);
        if (!groupId || !this.messageText.trim()) return;

        const content = this.messageText.trim();
        this.groupMsgSvc.sendMessage(groupId, content).subscribe({
            next: (msg) => {
                this.messages = [...this.messages, msg];
                this.messageText = '';
                this.scrollMessagesToBottom();
            }
        });
    }

    private scrollMessagesToBottom(): void {
        setTimeout(() => {
            const el = document.querySelector('.group-chat-body');
            if (el) el.scrollTop = el.scrollHeight;
        }, 30);
    }
}
