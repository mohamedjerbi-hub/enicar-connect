import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { Client, IMessage } from '@stomp/stompjs';
// Attention: sockjs-client peut nécessiter une importation globale ou * as
import SockJS from 'sockjs-client';
import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';

export interface ChatMessage {
    id?: number;
    senderId: number;
    recipientId: number;
    content: string;
    isRead?: boolean;
    timestamp?: string;
}

@Injectable({ providedIn: 'root' })
export class ChatService {
    private stompClient: Client;
    private http = inject(HttpClient);
    private authService = inject(AuthService);

    private currentUserId: number | null = null;

    get myId(): number | undefined {
        return this.currentUserId ?? this.authService.currentUser()?.id;
    }

    private _messages = new BehaviorSubject<ChatMessage[]>([]);
    public messages$ = this._messages.asObservable();

    constructor() {
        this.stompClient = new Client({
            // Note: on utilise wss:// ou ws:// directement si SockJS n'est pas activé côté server, 
            // mais comme @stomp/stompjs le supporte bien, on injecte SockJS ou l'URL WebSocket vanilla
            brokerURL: environment.wsUrl,
            // webSocketFactory: () => new SockJS(`${environment.apiUrl.replace('/api', '/ws')}`), // Décommentez si vous utilisez .withSockJS() en backend
            debug: () => {
                /* STOMP debug désactivé en livrable (pas de console.log) */
            },
            reconnectDelay: 2000,
        });

        this.stompClient.onConnect = () => {
            const userId = this.myId;
            if (userId == null) {
                return;
            }

            // S'abonner à SA propre file d'attente de messages personnels
            this.stompClient.subscribe(`/user/${userId}/queue/messages`, (message: IMessage) => {
                const newMessage: ChatMessage = JSON.parse(message.body);
                // Ajouter le nouveau message à l'UI
                this._messages.next([...this._messages.value, newMessage]);
            });
        };
    }

    public setCurrentUserId(userId: number): void {
        this.currentUserId = userId;
        if (!this.stompClient.active) {
            this.stompClient.activate();
        }
    }

    public getConversation(partnerId: number): void {
        this.http.get<ChatMessage[]>(`${environment.apiUrl}/messages/${partnerId}`)
            .subscribe({
                next: (msgs) => this._messages.next(msgs),
                error: (err) => {
                    console.error('Erreur API (Peut-être pas de messages)', err);
                    this._messages.next([]);
                }
            });
    }

    public sendMessage(recipientId: number, content: string): void {
        const currentId = this.myId;
        if (currentId == null) {
            console.error('Cannot send message: User not logged in.');
            return;
        }
        const msg: ChatMessage = {
            senderId: currentId,
            recipientId: recipientId,
            content: content
        };

        // Envoi au Backend STOMP Mapper @MessageMapping("/chat")
        this.stompClient.publish({
            destination: '/app/chat',
            body: JSON.stringify(msg)
        });

        // Afficher notre message envoyé directement dans l'interface
        this._messages.next([...this._messages.value, { ...msg, timestamp: new Date().toISOString() }]);
    }
}
