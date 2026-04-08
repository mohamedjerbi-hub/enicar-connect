export interface AppEvent {
    id: number;
    title: string;
    date: string;
    time: string;
    location: string;
    description: string;
    category: 'Conférence' | 'Hackathon' | 'Forum' | 'Atelier' | 'Soutenance' | 'Autre';
    organizer: string;
    registeredCount: number;
    registered: boolean;
    color: string;
    maxCapacity?: number;
    isOwner?: boolean;
}
