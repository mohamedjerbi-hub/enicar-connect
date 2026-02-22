import { Component, OnInit } from '@angular/core';
import { ChatService } from '../chat.service';

@Component({
    selector: 'app-chat-layout',
    templateUrl: './chat-layout.component.html',
    styleUrls: ['./chat-layout.component.css']
})
export class ChatLayoutComponent implements OnInit {

    constructor(private chatService: ChatService) { }

    ngOnInit(): void {
    }

}
