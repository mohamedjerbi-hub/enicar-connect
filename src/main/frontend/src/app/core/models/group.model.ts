export interface Group {
    id: number;
    name: string;
    description: string;
    groupType: string;
    privacy: string;
    icon: string;
    iconColor: string;
    bannerGradient: string;
    creatorId: number;
    creatorName: string;
    memberCount: number;
    joined: boolean;
    isOwner: boolean;
    myRole?: string;
    createdAt?: string;
}
