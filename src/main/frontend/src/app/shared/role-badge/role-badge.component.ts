import { Component, Input } from '@angular/core';
import { Role, ROLE_META } from '../../core/models/user.model';

@Component({
  selector: 'app-role-badge',
  standalone: true,
  templateUrl: './role-badge.component.html',
  styleUrl: './role-badge.component.css'
})
export class RoleBadgeComponent {
  @Input() role: string = 'student';
  get meta() { return ROLE_META[this.role as Role] ?? ROLE_META['student']; }
}
