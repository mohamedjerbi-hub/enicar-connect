import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private loggedIn = new BehaviorSubject<boolean>(false);

  get isLoggedIn() {
    return this.loggedIn.asObservable();
  }

  constructor(private router: Router) {
    // Restore session from localStorage
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      this.loggedIn.next(true);
    }
  }

  login(user: { email: string; password: string }): boolean {
    const email = (user.email || '').toLowerCase().trim();

    // Domain validation: only @enicar.ucar.tn emails are accepted
    if (!email.endsWith('@enicar.ucar.tn')) {
      return false;
    }

    // Demo credentials — replace with real API call in production
    const validUsers = [
      { email: 'admin@enicar.ucar.tn', password: 'admin123' },
      { email: 'mohamed.jerbi@enicar.ucar.tn', password: 'jerbi123' },
      { email: 'mohamed.babou@enicar.ucar.tn', password: 'babou123' },
      { email: 'dhia.abidi@enicar.ucar.tn', password: 'abidi123' },
    ];

    const found = validUsers.find(u => u.email === email && u.password === user.password);
    if (found) {
      this.loggedIn.next(true);
      localStorage.setItem('user', JSON.stringify({ email }));
      this.router.navigate(['/dashboard']);
      return true;
    }
    return false;
  }

  logout() {
    this.loggedIn.next(false);
    localStorage.removeItem('user');
    this.router.navigate(['/login']);
  }

  getCurrentUser(): any {
    const stored = localStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
  }
}
