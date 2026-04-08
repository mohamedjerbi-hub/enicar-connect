import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService, RegisterRequest } from '../../../core/services/auth.service';
import { ThemeService } from '../../../core/services/theme.service';
import { ParticlesBgComponent } from '../../../shared/particles-bg/particles-bg.component';

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink, ParticlesBgComponent],
    templateUrl: './register.component.html',
    styleUrl: './register.component.css'
})
export class RegisterComponent {
    firstName = '';
    lastName = '';
    email = '';
    password = '';
    confirmPassword = '';
    role = 'STUDENT';
    department = '';
    level = '';
    phone = '';

    error = '';
    loading = false;

    readonly theme = inject(ThemeService);
    private auth = inject(AuthService);
    private router = inject(Router);

    readonly roles = [
        { value: 'STUDENT', label: 'Étudiant', icon: 'fa-graduation-cap' },
        { value: 'TEACHER', label: 'Enseignant', icon: 'fa-chalkboard-teacher' },
        { value: 'ADMIN_STAFF', label: 'Personnel Administratif', icon: 'fa-university' },
        { value: 'DIRECTION', label: 'Direction', icon: 'fa-building' },
        { value: 'ALUMNI', label: 'Ancien Étudiant', icon: 'fa-user-tie' },
    ];

    readonly departments = [
        'Informatique', 'Génie Civil', 'Génie Mécanique',
        'Génie Électrique', 'Génie Industriel', 'Autre'
    ];

    readonly levels = [
        '1ère année', '2ème année', '3ème année',
        'Mastère 1', 'Mastère 2', 'Doctorat', 'Autre'
    ];

    async onRegister(): Promise<void> {
        this.error = '';

        if (this.password !== this.confirmPassword) {
            this.error = 'Les mots de passe ne correspondent pas';
            return;
        }

        if (this.password.length < 6) {
            this.error = 'Le mot de passe doit contenir au moins 6 caractères';
            return;
        }

        this.loading = true;

        const request: RegisterRequest = {
            firstName: this.firstName,
            lastName: this.lastName,
            email: this.email,
            password: this.password,
            role: this.role,
            phone: this.phone || undefined,
            department: this.department || undefined,
            level: this.level || undefined,
        };

        const result = await this.auth.register(request);

        if (result.success) {
            this.router.navigate(['/feed']);
        } else {
            this.error = result.error || 'Erreur lors de l\'inscription';
        }

        this.loading = false;
    }
}
