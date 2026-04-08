import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../../shared/navbar/navbar.component';
import { ParticlesBgComponent } from '../../shared/particles-bg/particles-bg.component';
import { MentorshipService, MentorshipDTO, MentorDTO } from '../../core/services/mentorship.service';

@Component({
    selector: 'app-mentorship',
    standalone: true,
    imports: [CommonModule, FormsModule, NavbarComponent, ParticlesBgComponent],
    templateUrl: './mentorship.component.html',
    styleUrl: './mentorship.component.css'
})
export class MentorshipComponent implements OnInit {
    private mentorshipSvc = inject(MentorshipService);

    activeTab: 'MENTORS' | 'MY_REQUESTS' = 'MENTORS';

    availableMentors: MentorDTO[] = [];
    myRequests: MentorshipDTO[] = [];

    showModal = false;
    selectedMentor: MentorDTO | null = null;
    objectiveText = '';

    ngOnInit() {
        this.loadMentors();
        this.loadMyRequests();
    }

    loadMentors() {
        this.mentorshipSvc.getAvailableMentors().subscribe(m => this.availableMentors = m);
    }

    loadMyRequests() {
        this.mentorshipSvc.getMyRequestsAsMentee().subscribe(r => this.myRequests = r);
    }

    openRequestModal(mentor: MentorDTO) {
        this.selectedMentor = mentor;
        this.objectiveText = '';
        this.showModal = true;
    }

    submitRequest() {
        if (!this.selectedMentor || !this.objectiveText.trim()) return;
        this.mentorshipSvc.requestMentorship(this.selectedMentor.id, this.objectiveText.trim()).subscribe(() => {
            this.showModal = false;
            this.loadMyRequests();
            this.activeTab = 'MY_REQUESTS';
        });
    }

    getStatusClass(status: string) {
        return {
            'PENDING': 'status-pending',
            'ACTIVE': 'status-active',
            'COMPLETED': 'status-completed',
            'REJECTED': 'status-rejected'
        }[status] || 'status-pending';
    }
}
