import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface NetworkConnection {
    id: number;
    firstName: string;
    lastName: string;
    role: string;
    department: string;
    level: string;
}

export interface ConnectionReq {
    id: number;
    sender: NetworkConnection;
    status: string;
    timestamp: string;
}

@Injectable({ providedIn: 'root' })
export class NetworkService {
    private readonly API = `${environment.apiUrl}/network`;
    private http = inject(HttpClient);

    // Récupérer la liste des connexions approuvées (le réseau)
    getMyNetwork(): Observable<NetworkConnection[]> {
        return this.http.get<NetworkConnection[]>(`${this.API}/connections`);
    }

    // Récupérer les invitations reçues en attente
    getPendingRequests(): Observable<ConnectionReq[]> {
        return this.http.get<ConnectionReq[]>(`${this.API}/requests`);
    }

    // Envoyer une demande de connexion à un utilisateur
    sendRequest(userId: number): Observable<{ message: string }> {
        return this.http.post<{ message: string }>(`${this.API}/request/${userId}`, {});
    }

    // Accepter une demande (requestId)
    acceptRequest(requestId: number): Observable<{ message: string }> {
        return this.http.post<{ message: string }>(`${this.API}/accept/${requestId}`, {});
    }

    // Refuser/Ignorer une demande
    rejectRequest(requestId: number): Observable<{ message: string }> {
        return this.http.post<{ message: string }>(`${this.API}/reject/${requestId}`, {});
    }
}
