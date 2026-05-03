import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GroupMessage } from '../models/group-message.model';

@Injectable({ providedIn: 'root' })
export class GroupMessageService {
  private http = inject(HttpClient);
  private readonly API = 'http://localhost:8081/api/groups';

  getMessages(groupId: number): Observable<GroupMessage[]> {
    return this.http.get<GroupMessage[]>(`${this.API}/${groupId}/messages`);
  }

  sendMessage(groupId: number, content: string): Observable<GroupMessage> {
    return this.http.post<GroupMessage>(`${this.API}/${groupId}/messages`, { content });
  }
}

