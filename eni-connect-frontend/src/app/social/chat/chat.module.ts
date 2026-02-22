import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SharedModule } from '../../shared/shared.module';
import { ChatLayoutComponent } from './chat-layout/chat-layout.component';
import { ChatListComponent } from './chat-list/chat-list.component';
import { ChatWindowComponent } from './chat-window/chat-window.component';
import { ChatService } from './chat.service';

@NgModule({
    declarations: [
        ChatLayoutComponent,
        ChatListComponent,
        ChatWindowComponent
    ],
    imports: [
        CommonModule,
        FormsModule,
        SharedModule
    ],
    providers: [
        ChatService
    ],
    exports: [
        ChatLayoutComponent
    ]
})
export class ChatModule { }
