import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent {
    errorMessage: string = '';

    constructor(private authService: AuthService, private router: Router) { }

    onSubmit(form: any) {
        const email: string = form.value.email || '';
        const password: string = form.value.password || '';

        // Validate institutional email domain
        if (!email.toLowerCase().endsWith('@enicar.ucar.tn')) {
            this.errorMessage = 'Seules les adresses email @enicar.ucar.tn sont autorisées.';
            return;
        }

        if (this.authService.login({ email, password })) {
            this.errorMessage = '';
        } else {
            this.errorMessage = 'Email ou mot de passe incorrect. Vérifiez vos identifiants.';
        }
    }
}
