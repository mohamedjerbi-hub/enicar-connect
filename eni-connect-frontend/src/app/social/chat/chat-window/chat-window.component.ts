import { Component, OnInit, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { Observable, of } from 'rxjs';
import { switchMap, map } from 'rxjs/operators';
import { ChatService } from '../chat.service';
import { Message, Thread } from '../models'; // Import User if needed

@Component({
    selector: 'app-chat-window',
    templateUrl: './chat-window.component.html', // Fixed typo here
    styleUrls: ['./chat-window.component.css']
})
export class ChatWindowComponent implements OnInit, AfterViewChecked {
    @ViewChild('scrollContainer') private scrollContainer!: ElementRef;

    activeThread$: Observable<Thread | undefined>;
    messages$: Observable<Message[]>;
    newMessage: string = '';
    private currentThreadId: string | null = null;

    constructor(private chatService: ChatService) {
        this.activeThread$ = this.chatService.getActiveThreadId().pipe(
            switchMap(id => {
                this.currentThreadId = id;
                if (!id) return of(undefined);
                return this.chatService.getThreads().pipe(
                    map(threads => threads.find(t => t.id === id))
                );
            })
        );

        this.messages$ = this.chatService.getActiveThreadId().pipe(
            switchMap(id => id ? this.chatService.getMessages(id) : of([]))
        );
    }

    ngOnInit(): void {
        this.scrollToBottom();
    }

    ngAfterViewChecked() {
        this.scrollToBottom();
    }

    sendMessage() {
        if (!this.newMessage.trim() || !this.currentThreadId) return;

        this.chatService.sendMessage(this.currentThreadId, this.newMessage).then(() => {
            this.newMessage = '';
            this.scrollToBottom();
        });
    }

    scrollToBottom(): void {
        try {
            if (this.scrollContainer) {
                this.scrollContainer.nativeElement.scrollTop = this.scrollContainer.nativeElement.scrollHeight;
            }
        } catch (err) { }
    }

    getSenderName(senderId: string, thread: Thread): string {
        const participant = thread.participants.find(p => p.id === senderId);
        return participant ? participant.name : 'Inconnu';
    }
}
