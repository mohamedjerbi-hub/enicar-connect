export interface Resource {
    id: number;
    title: string;
    author: string;
    date: string;
    size: string;
    icon: string;
    category: 'Cours' | 'TD / TP' | 'Examens' | 'PFE';
    isOwner?: boolean;
}
