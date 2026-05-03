export interface Job {
    id: number;
    title: string;
    company: string;
    location: string;
    type: 'CDI' | 'CDD' | 'Stage' | 'Alternance' | 'Freelance';
    tags: string[];
    requiredSkills?: string[];
    description: string;
    posted: string;
    applied: boolean;
    isOwner?: boolean;
    compatibilityScore?: number;
    matchedSkills?: string[];
}
