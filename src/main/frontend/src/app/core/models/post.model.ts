import { Role } from './user.model';

export interface CommentData {
    id: number;
    authorId: number;
    authorName: string;
    authorInitials: string;
    authorRole: string;
    authorAvatarColor: string;
    authorAvatarBg: string;
    text: string;
    createdAt: string;
}

export interface MentionData {
    id: number;
    name: string;
    initials: string;
}

export interface Post {
    id: number;
    authorId: number;
    authorName: string;
    authorInitials: string;
    authorRole: string;
    authorAvatarColor: string;
    authorAvatarBg: string;
    body: string;
    visibility: string;
    hashtags: string[];
    mediaUrls: string[];
    mentions: MentionData[];
    likesCount: number;
    likedByMe: boolean;
    commentsCount: number;
    comments: CommentData[];
    reportsCount: number;
    moderated: boolean;
    createdAt: string;
    updatedAt: string;

    // UI-only state
    commentsOpen?: boolean;
}

// Legacy alias kept for backward compatibility in templates
export type Comment = CommentData;
