import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { GroupService } from '../../../core/services/group.service';
import { Group } from '../../../core/models/group.model';
import { PostService } from '../../../core/services/post.service';
import { Post } from '../../../core/models/post.model';
import { NavbarComponent } from '../../../shared/navbar/navbar.component';
import { ParticlesBgComponent } from '../../../shared/particles-bg/particles-bg.component';
import { RoleBadgeComponent } from '../../../shared/role-badge/role-badge.component';

@Component({
    selector: 'app-group-detail',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink, NavbarComponent, ParticlesBgComponent, RoleBadgeComponent],
    templateUrl: './group-detail.component.html',
    styleUrl: './group-detail.component.css'
})
export class GroupDetailComponent implements OnInit {
    private route = inject(ActivatedRoute);
    private groupSvc = inject(GroupService);
    private postSvc = inject(PostService);

    group: Group | null = null;
    posts: Post[] = [];
    activeTab = 'feed'; // feed, members, resources, events
    composerText = '';

    ngOnInit(): void {
        const id = this.route.snapshot.params['id'];
        this.groupSvc.getAll().subscribe(groups => {
            this.group = groups.find(g => g.id === +id) || null;
        });
        // For now, GroupFeed is just a filter on all posts or a separate service
        // In real app, we fetch from /api/posts?groupId=id
    }

    postText(): void {
        if (!this.composerText.trim() || !this.group) return;
        // this.postSvc.createPost(this.composerText.trim(), 'GROUP', this.group.id);
        this.composerText = '';
    }
}
