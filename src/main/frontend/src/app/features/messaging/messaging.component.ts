import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { ToastComponent } from '../../shared/toast/toast.component';
import { ChatService, ChatMessage } from '../../core/services/chat.service';
import { AuthService } from '../../core/services/auth.service';

interface Conversation {
  id: number;
  initials: string;
  name: string;
  online: boolean;
  preview?: string;
  time?: string;
  unread?: number;
}

@Component({
  selector: 'app-messaging',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, ToastComponent],
  templateUrl: './messaging.component.html',
  styleUrl: './messaging.component.css'
})
export class MessagingComponent implements OnInit {
  private chatService = inject(ChatService);
  private http = inject(HttpClient);
  private auth = inject(AuthService);

  conversations: Conversation[] = [];
  filteredConversations: Conversation[] = [];
  activeConv: Conversation | null = null;
  activeMessages: ChatMessage[] = [];
  inputText = '';
  searchQuery = '';
  myId = this.auth.currentUser()?.id || 1;

  constructor() {
    this.chatService.myId = this.myId;
  }

  ngOnInit() {
    this.loadDirectory();
    this.chatService.messages$.subscribe(msgs => {
      this.activeMessages = msgs;
      this.scrollToBottom();
    });
  }

  loadDirectory() {
    this.http.get<any[]>('http://localhost:8081/api/users/directory').subscribe({
      next: (users) => {
        this.conversations = users
          .filter(u => u.id !== this.myId)
          .map(u => ({
            id: u.id,
            name: `${u.firstName} ${u.lastName}`,
            initials: `${u.firstName.charAt(0)}${u.lastName.charAt(0)}`.toUpperCase(),
            online: true,
            preview: 'Démarrer une conversation...',
            time: ''
          }));
        this.filteredConversations = [...this.conversations];
      }
    });
  }

  filterUsers() {
    const q = this.searchQuery.toLowerCase();
    this.filteredConversations = this.conversations.filter(c =>
      c.name.toLowerCase().includes(q)
    );
  }

  selectConv(c: Conversation): void {
    this.activeConv = c;
    c.unread = undefined;
    this.chatService.getConversation(c.id);
    this.scrollToBottom();
  }

  sendMessage(): void {
    if (!this.inputText.trim() || !this.activeConv) return;
    this.chatService.sendMessage(this.activeConv.id, this.inputText.trim());
    this.inputText = '';
    this.scrollToBottom();
  }

  scrollToBottom() {
    setTimeout(() => {
      const container = document.querySelector('.messages-wrapper');
      if (container) container.scrollTop = container.scrollHeight;
    }, 50);
  }
}
