import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { ChatService } from '../chat.service';
import { Thread, User } from '../models';

@Component({
    selector: 'app-chat-list',
    templateUrl: './chat-list.component.html',
    styleUrls: ['./chat-list.component.css']
})
export class ChatListComponent implements OnInit {
    threads$: Observable<Thread[]>;
    activeThreadId$: Observable<string | null>;
    currentUser: User;
    searchTerm: string = '';

    constructor(
        private chatService: ChatService,
        private route: ActivatedRoute
    ) {
        this.threads$ = this.chatService.getThreads();
        this.activeThreadId$ = this.chatService.getActiveThreadId();
        this.currentUser = this.chatService.getCurrentUser();
    }

    ngOnInit(): void {
        this.route.queryParams.subscribe((params: any) => {
            if (params['threadId']) {
                this.chatService.setActiveThread(params['threadId']);
            }
        });
    }

    onThreadClick(threadId: string) {
        this.chatService.setActiveThread(threadId);
    }
}
