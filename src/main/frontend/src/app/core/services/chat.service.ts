import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { Client, IMessage } from '@stomp/stompjs';
// Attention: sockjs-client peut nécessiter une importation globale ou * as
import SockJS from 'sockjs-client';

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

    // ID mocké de l'utilisateur connecté (pour la démonstration).
    // À remplacer dynamiquement par "auth.service.getCurrentUserId()"
    public myId = 1;

    private _messages = new BehaviorSubject<ChatMessage[]>([]);
    public messages$ = this._messages.asObservable();

    constructor() {
        this.stompClient = new Client({
            // Note: on utilise wss:// ou ws:// directement si SockJS n'est pas activé côté server, 
            // mais comme @stomp/stompjs le supporte bien, on injecte SockJS ou l'URL WebSocket vanilla
            brokerURL: 'ws://localhost:8081/ws',
            // webSocketFactory: () => new SockJS('http://localhost:8081/ws'), // Décommentez si vous utilisez .withSockJS() en backend
            debug: (msg: string) => console.log('STOMP: ' + msg),
            reconnectDelay: 2000,
        });

        this.stompClient.onConnect = (frame) => {
            console.log('✅ Connecté au WebSocket !', frame);

            // S'abonner à SA propre file d'attente de messages personnels
            this.stompClient.subscribe(`/user/${this.myId}/queue/messages`, (message: IMessage) => {
                const newMessage: ChatMessage = JSON.parse(message.body);
                // Ajouter le nouveau message à l'UI
                this._messages.next([...this._messages.value, newMessage]);
            });
        };

        this.stompClient.activate();
    }

    public getConversation(partnerId: number): void {
        this.http.get<ChatMessage[]>(`http://localhost:8081/api/messages/${partnerId}`)
            .subscribe({
                next: (msgs) => this._messages.next(msgs),
                error: (err) => {
                    console.error('Erreur API (Peut-être pas de messages)', err);
                    this._messages.next([]);
                }
            });
    }

    public sendMessage(recipientId: number, content: string): void {
        const msg: ChatMessage = {
            senderId: this.myId,
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
