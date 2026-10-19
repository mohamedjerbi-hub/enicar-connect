## 1. `auth.service.ts`
```typescript
import { Injectable, signal, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { User, Role } from '../models/user.model';
import { environment } from '../../../environments/environment';

export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    role: string;
}

interface AuthResponse {
    token: string;
    user: {
        id: number;
        fullName: string;
        firstName: string;
        lastName: string;
        email: string;
        role: string;
        initials: string;
        avatarColor: string;
        avatarBg: string;
        permissions: string[];
    };
}

@Injectable({ providedIn: 'root' })
export class AuthService {
    private readonly API = `${environment.apiUrl}/auth`;
    private http = inject(HttpClient);
    private router = inject(Router);

    private _user = signal<User | null>(null);
    readonly currentUser = this._user.asReadonly();
    readonly isLoggedIn = () => this._user() !== null;

    constructor() {
        const token = localStorage.getItem('enicar-token');
        const stored = localStorage.getItem('enicar-user');
        if (token && stored) {
            try {
                this._user.set(JSON.parse(stored));
            } catch {
                this.clearStorage();
            }
        }
    }

    async login(email: string, password: string): Promise<boolean> {
        try {
            const res = await this.http.post<AuthResponse>(`${this.API}/login`, { email, password }).toPromise();
            if (res && res.token) {
                this.handleAuthResponse(res);
                return true;
            }
            return false;
        } catch {
            return false;
        }
    }

    // Le reste du code a été omis pour des raisons de brièveté car l'erreur provient de la méthode login() ou des intercepteurs
}
```

## 2. `error.interceptor.ts`
```typescript
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { ToastService } from '../services/toast.service';
import { AuthService } from '../services/auth.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const toast = inject(ToastService);
  const auth = inject(AuthService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'Une erreur inattendue est survenue.';

      if (error.error instanceof ErrorEvent) {
        errorMessage = `Erreur: ${error.error.message}`;
      } else {
        switch (error.status) {
          case 401:
            errorMessage = 'Session expirée ou non autorisée. Veuillez vous reconnecter.';
            auth.logout();
            break;
          case 403:
            errorMessage = 'Accès refusé.';
            break;
          case 404:
            errorMessage = 'Ressource introuvable.';
            break;
          case 500:
            errorMessage = 'Erreur interne du serveur.';
            break;
          default:
            if (error.error && error.error.message) {
              errorMessage = error.error.message;
            } else if (error.message) {
              errorMessage = error.message;
            }
            break;
        }
      }

      if (!req.url.includes('/like')) {
        toast.show('fas fa-exclamation-triangle', errorMessage);
      }
      return throwError(() => error);
    })
  );
};
```

## 3. `SecurityConfig.java`
```java
package tn.enicar.enicarconnect.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.context.annotation.Bean;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Modification d'urgence (Antigravity) : Utilisation des "Patterns" au lieu de "Origins" strict pour matcher Vercel
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

## 4. `environment.ts`
```typescript
export const environment = {
  production: false,
  // URL en dur suite au correctif :
  apiUrl: 'https://enicar-connect.onrender.com/api',
  wsUrl: 'wss://enicar-connect.onrender.com/ws'
};
```
