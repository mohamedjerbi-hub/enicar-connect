export interface User {
    id: string;
    name: string;
    avatar: string;
    status: 'online' | 'offline' | 'busy';
}

export interface Message {
    id: string;
    senderId: string;
    text: string;
    timestamp: Date;
    isMe: boolean;
}

export interface Thread {
    id: string;
    name: string;
    avatar: string;
    type: 'group' | 'dm';
    participants: User[];
    messages: Message[];
    lastMessage?: Message;
    unreadCount: number;
}
