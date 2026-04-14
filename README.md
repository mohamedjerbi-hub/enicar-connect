# ENICAR Connect

> **Plateforme communautaire officielle de l'École Nationale d'Ingénieurs de Carthage**

![ENI Carthage](eni-connect/src/assets/images/enicar-official-logo.jpg)

[![Angular](https://img.shields.io/badge/Frontend-Angular%2015-red?logo=angular)](https://angular.io)
[![Spring Boot](https://img.shields.io/badge/Backend-Spring%20Boot%203-green?logo=springboot)](https://spring.io)
[![MySQL](https://img.shields.io/badge/Database-MySQL-blue?logo=mysql)](https://www.mysql.com)
[![License](https://img.shields.io/badge/License-ENI%20Carthage-gold)](.)

---

## Équipe — INFO2 Groupe C

| Membre | Rôle | Modules |
|--------|------|---------|
| **Mohamed Jerbi** | Chef de projet | Auth & Sécurité · Admin & Modération |
| **Mohamed Babou** | Développeur | Réseau Social · Réseau Professionnel |
| **Mohamed Dhia Islem Abidi** | Développeur | QR Code & Présences · Notes & Dashboard |

---

## Modules

| # | Module | Responsable | Statut |
|---|--------|-------------|--------|
| 1 | Authentification & Sécurité (`@enicar.ucar.tn`) | Jerbi | 🟡 En cours |
| 2 | Administration & Modération | Jerbi | 🟡 En cours |
| 3 | Réseau Social (Feed, Events, Messagerie) | Babou | ⬜ À faire |
| 4 | Réseau Professionnel (Jobs, Mentorat) | Babou | ⬜ À faire |
| 5 | QR Code Présences & Réservations | Abidi | 🟡 En cours |
| 6 | Notes, Réclamations & Dashboard | Abidi | 🟡 En cours |

---

## Contributions — Mohamed Dhia Islem Abidi

### Module 5 — QR Code Présences & Réservations (`#5`)

**Backend (Spring Boot)**
- `Attendance.java` — Entité JPA représentant une présence enregistrée
- `AttendanceRepository.java` — Repository JPA (recherche par email étudiant / code cours)
- `AttendanceService.java` — Logique métier : enregistrement de présence via scan QR
- `AttendanceController.java` — REST API :
  - `POST /api/attendance/scan` — Enregistrer une présence (payload : studentEmail, courseCode)
  - `GET /api/attendance/student/{email}` — Présences d'un étudiant
  - `GET /api/attendance/course/{code}` — Présences par cours

### Module 6 — Notes, Réclamations & Dashboard (`#6`)

**Backend (Spring Boot)**
- `Grade.java` — Entité JPA représentant une note (matière, score, semestre)
- `GradeRepository.java` — Repository JPA (recherche par email / semestre)
- `GradeController.java` — REST API :
  - `GET /api/grades/student/{email}` — Notes d'un étudiant
  - `POST /api/grades` — Ajouter une note

**Frontend (Angular)**
- `dashboard.component.ts` — Chargement des notes et présences via HTTP
- `dashboard.component.html` — Affichage : stats globales, tableau de notes, tableau de présences
- `dashboard.component.css` — UI responsive avec indicateurs colorés (vert/rouge)

---

## Structure du dépôt
ENICAR-Connect/
├── eni-connect-frontend/ # Frontend Angular
│   ├── src/
│   │   ├── app/
│   │   │   ├── auth/         # Module 1 — Jerbi
│   │   │   ├── dashboard/    # Module 6 — Abidi
│   │   │   │   ├── dashboard.component.ts
│   │   │   │   ├── dashboard.component.html
│   │   │   │   └── dashboard.component.css
│   │   │   ├── social/       # Module 3 — Babou
│   │   │   ├── professional/ # Module 4 — Babou
│   │   │   └── shared/       # Header, Footer communs
│   │   ├── assets/
│   │   └── styles.css
│   ├── angular.json
│   └── package.json
│
├── eni-connect-backend/  # Backend Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/enicar/connect/
│   │   │   │       ├── controller/
│   │   │   │       │   ├── AttendanceController.java   # Abidi — Module 5
│   │   │   │       │   └── GradeController.java        # Abidi — Module 6
│   │   │   │       ├── model/
│   │   │   │       │   ├── Attendance.java             # Abidi — Module 5
│   │   │   │       │   └── Grade.java                  # Abidi — Module 6
│   │   │   │       ├── repository/
│   │   │   │       │   ├── AttendanceRepository.java   # Abidi — Module 5
│   │   │   │       │   └── GradeRepository.java        # Abidi — Module 6
│   │   │   │       └── service/
│   │   │   │           └── AttendanceService.java      # Abidi — Module 5
│   │   │   └── resources/
│   │   └── test/
│   ├── database.sql
│   └── pom.xml
│
└── README.md

---

## Démarrage rapide

### Prérequis
- **Node.js** ≥ 18.x + npm
- **Java** 17+
- **Maven** 3.8+
- **MySQL** 8.x

### Frontend Angular
```bash
cd eni-connect-frontend
npm install
npm start
# → http://localhost:4200
```

### Backend Spring Boot
```bash
cd eni-connect-backend
# 1. Créer la base de données MySQL :
mysql -u root -p < database.sql
# 2. Configurer src/main/resources/application.properties
# 3. Lancer le backend :
mvn spring-boot:run
# → http://localhost:8080
```

### Comptes de démonstration
| Email | Mot de passe | Rôle |
|-------|-------------|------|
| `admin@enicar.ucar.tn` | `admin123` | Admin |
| `mohamed.jerbi@enicar.ucar.tn` | `jerbi123` | Étudiant |
| `mohamed.babou@enicar.ucar.tn` | `babou123` | Étudiant |
| `dhia.abidi@enicar.ucar.tn` | `abidi123` | Étudiant |

> **Seules les adresses `@enicar.ucar.tn` sont autorisées**

---

## Stratégie de branches
main        ← Production stable (protégée)
develop     ← Intégration continue
├── feature/auth           (Jerbi)
├── feature/admin          (Jerbi)
├── feature/social         (Babou)
├── feature/professional   (Babou)
├── feature/qr-code        (Abidi)
└── feature/notes-services (Abidi)

**Règles de commit :**  
`feat: description` · `fix: description` · `docs: description` · `refactor: description`

---

## Stack technique

| Couche | Tech | Version |
|--------|------|---------|
| Frontend | Angular | 15+ |
| Backend | Spring Boot | 3.x |
| Base de données | MySQL | 8.x |
| Auth | Spring Security + JWT | — |
| QR Code | ZXing (Java) | — |
| Temps réel | WebSockets (STOMP) | — |
| CI/CD | GitHub Actions | — |

---

*ENI Carthage — INFO2 Groupe C — 2025/2026*
