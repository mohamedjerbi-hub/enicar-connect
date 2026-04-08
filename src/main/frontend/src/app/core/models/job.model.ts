export interface Job {
    id: number;
    title: string;
    company: string;
    location: string;
    type: 'CDI' | 'CDD' | 'Stage' | 'Alternance' | 'Freelance';
    tags: string[];
    description: string;
    posted: string;
    applied: boolean;
    isOwner?: boolean;
}
