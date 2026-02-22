import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
    selector: 'app-groups',
    templateUrl: './groups.component.html',
    styleUrls: ['./groups.component.css']
})
export class GroupsComponent {
    constructor(private router: Router) { }

    openChat(groupId: string) {
        this.router.navigate(['/messages'], { queryParams: { threadId: groupId } });
    }
}
