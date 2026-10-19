import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ThemeService } from '../../../core/services/theme.service';
import { ParticlesBgComponent } from '../../../shared/particles-bg/particles-bg.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, ParticlesBgComponent, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';
  loading = false;

  private auth = inject(AuthService);
  readonly theme = inject(ThemeService);
  private router = inject(Router);

  async onLogin(): Promise<void> {
    this.error = '';
    this.loading = true;

    const result = await this.auth.login(this.email, this.password);

    if (result.success) {
      this.router.navigate(['/feed']);
    } else {
      this.error = result.errorMessage ?? 'Email ou mot de passe incorrect (valeur par défaut).';
    }

    this.loading = false;
  }
}
