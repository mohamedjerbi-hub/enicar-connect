import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { Thread, Message, User } from './models';

@Injectable({
    providedIn: 'root'
})
export class ChatService {

    // Mock Current User
    private currentUser: User = {
        id: 'u1',
        name: 'Mohamed Jerbi',
        avatar: 'https://ui-avatars.com/api/?name=Mohamed+Jerbi&background=0078D4&color=fff', // Fixed avatar
        status: 'online'
    };

    // Mock Users
    private users: User[] = [
        { id: 'u2', name: 'Ahmed Ben Ali', avatar: 'https://ui-avatars.com/api/?name=Ahmed+Ben+Ali', status: 'offline' },
        { id: 'u3', name: 'Sara Tounsi', avatar: 'https://ui-avatars.com/api/?name=Sara+Tounsi', status: 'online' },
        { id: 'u4', name: 'Karim Oueslati', avatar: 'https://ui-avatars.com/api/?name=Karim+Oueslati', status: 'busy' }
    ];

    // Mock Threads (Channels)
    private threadsSubject = new BehaviorSubject<Thread[]>([
        {
            id: 'g1',
            name: 'Génie Informatique 2026',
            avatar: 'https://ui-avatars.com/api/?name=GI+26&background=0D8ABC&color=fff',
            type: 'group',
            participants: [this.currentUser, this.users[0], this.users[1], this.users[2]],
            messages: [],
            unreadCount: 0,
            lastMessage: {
                id: 'm1',
                senderId: 'u2',
                text: 'N\'oubliez pas la réunion de demain !',
                timestamp: new Date(Date.now() - 3600000), // 1 hour ago
                isMe: false
            }
        },
        {
            id: 'g2',
            name: '2INFO-B', // Changed from 4INFO-B
            avatar: 'https://ui-avatars.com/api/?name=2INFO+B&background=random',
            type: 'group',
            participants: [this.currentUser, this.users[0], this.users[2]],
            messages: [],
            unreadCount: 2,
            lastMessage: {
                id: 'm2',
                senderId: 'u3',
                text: 'Quelqu\'un a le cours de Java ?',
                timestamp: new Date(Date.now() - 1800000), // 30 mins ago
                isMe: false
            }
        },
        {
            id: 'dm1',
            name: 'Ahmed Ben Ali',
            avatar: 'https://ui-avatars.com/api/?name=Ahmed+Ben+Ali',
            type: 'dm',
            participants: [this.currentUser, this.users[0]],
            messages: [],
            unreadCount: 0,
            lastMessage: {
                id: 'm3',
                senderId: 'u2',
                text: 'Salut, ça va ?',
                timestamp: new Date(Date.now() - 86400000), // 1 day ago
                isMe: false
            }
        }
    ]);

    private activeThreadId = new BehaviorSubject<string | null>(null);

    constructor() { }

    getThreads(): Observable<Thread[]> {
        return this.threadsSubject.asObservable();
    }

    getActiveThreadId(): Observable<string | null> {
        return this.activeThreadId.asObservable();
    }

    setActiveThread(threadId: string) {
        this.activeThreadId.next(threadId);
        // Mark as read logic would go here
        const threads = this.threadsSubject.value;
        const threadIndex = threads.findIndex(t => t.id === threadId);
        if (threadIndex > -1) {
            threads[threadIndex].unreadCount = 0;
            this.threadsSubject.next([...threads]);
        }
    }

    getMessages(threadId: string): Observable<Message[]> {
        // In a real app, this would fetch from API
        // For mock, we simply return the messages of the thread or generate some default ones if empty
        const thread = this.threadsSubject.value.find(t => t.id === threadId);
        if (thread && thread.messages.length === 0) {
            // Populate with some existing messages if empty for demo
            if (thread.lastMessage) {
                return of([thread.lastMessage]);
            }
            return of([]);
        }
        return of(thread ? thread.messages : []);
    }

    sendMessage(threadId: string, text: string): Promise<void> {
        return new Promise((resolve) => {
            const threads = this.threadsSubject.value;
            const threadIndex = threads.findIndex(t => t.id === threadId);

            if (threadIndex > -1) {
                const newMessage: Message = {
                    id: Math.random().toString(36).substr(2, 9),
                    senderId: this.currentUser.id,
                    text: text,
                    timestamp: new Date(),
                    isMe: true
                };

                const thread = threads[threadIndex];
                thread.messages = [...thread.messages, newMessage];
                thread.lastMessage = newMessage;

                // Update the subject
                threads[threadIndex] = thread;
                // Move this thread to top
                threads.sort((a, b) => {
                    const timeA = a.lastMessage?.timestamp.getTime() || 0;
                    const timeB = b.lastMessage?.timestamp.getTime() || 0;
                    return timeB - timeA;
                });

                this.threadsSubject.next([...threads]);
                resolve();

                // Simulate reply for demo
                if (thread.type === 'dm') {
                    setTimeout(() => {
                        this.receiveMockReply(threadId);
                    }, 2000);
                }
            }
        });
    }

    private receiveMockReply(threadId: string) {
        const threads = this.threadsSubject.value;
        const threadIndex = threads.findIndex(t => t.id === threadId);
        if (threadIndex > -1) {
            const thread = threads[threadIndex];
            const reply: Message = {
                id: Math.random().toString(36).substr(2, 9),
                senderId: thread.participants.find(p => p.id !== this.currentUser.id)?.id || 'unknown',
                text: 'C\'est noté ! (Réponse automatique)',
                timestamp: new Date(),
                isMe: false
            };
            thread.messages = [...thread.messages, reply];
            thread.lastMessage = reply;
            thread.unreadCount = (thread.id !== this.activeThreadId.value) ? (thread.unreadCount + 1) : 0;
            this.threadsSubject.next([...threads]);
        }
    }

    getCurrentUser(): User {
        return this.currentUser;
    }
}
