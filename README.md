<div align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Angular-17.0+-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="Angular" />
  <img src="https://img.shields.io/badge/PostgreSQL-15-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
</div>

<h1 align="center">ENICAR Connect 🏛️</h1>

<p align="center">
  <strong>The Ultimate Centralized Digital Community for École Nationale d'Ingénieurs de Carthage (ENI Carthage)</strong>
</p>

---

## 📋 Project Overview

**ENICAR Connect** is a centralized, robust web platform designed to replace scattered, decentralized communication channels (WhatsApp, Facebook) across the ENI Carthage ecosystem. By completely digitalizing administrative procedures and bridging the gap between students, educators, and alumni, the platform actively drives academic success and professional networking.

The application serves three distinct vertical pillars:

1. 🌐 **Internal Social Network:** Interactive newsfeed, deep group management, scalable event planning, and real-time instant messaging.
2. 💼 **Professional Network:** Dynamic CV profiling, student-to-alumni connections, verified internship/job postings, and an algorithmic mentorship program.
3. 🛠️ **Administrative & Utility Services:** Digital role-based administration, automated PDF document requests, and centralized facility management.

---

## 🛠️ Architecture & Technology Stack

The project follows an Enterprise-grade monolithic architecture. Rather than maintaining heavy independent repositories, the **Angular application is fully embedded within the Spring Boot ecosystem**, offering simple compilation, zero strict CORS complexities in production, and unified containerization.

- **Backend:** Java 17 | Spring Boot 3 | Spring Security (JWT Stateless Authentication)
- **Frontend:** Angular 17+ | Tailwind CSS | RxJS | STOMP WebSockets
- **Persistence:** PostgreSQL 15 (Production) | H2 Database (Local Dev)
- **Infrastructure:** Docker | Docker Compose | Flyway Migrations | Maven Build Automations

---

## 🚀 Getting Started

### Prerequisites
Make sure you have the following installed to run the application natively:
- **Java 17+**
- **Node.js 20+**
- **Docker 24+** (Required for production environments)

### 1. Unified Local Development
The application compiles both the Javascript Frontend and the Java Backend automatically.

```bash
# 1. Ask Maven to install Node, build the Angular Prod artifacts, and package the Spring JAR
.\mvnw.cmd clean install -DskipTests

# 2. Boot the Unified Application (Default: H2 Memory Mode)
.\mvnw.cmd spring-boot:run

# 3. Boot with PostgreSQL (Requires Docker for DB)
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres
```
> 📍 **App lives at**: `http://localhost:8081`

*Note: For granular Frontend UI (Hot-Reloading), enter `src/main/frontend` and execute `npm start` (Runs on Port 4200).*

### 2. Production Docker Deployment
ENICAR Connect is built for immediate CI/CD staging. A single command handles pulling PostgreSQL, triggering the Flyway schema migrations, and serving the optimized JAR.

```bash
# Spin up the containers in detached mode
docker-compose up --build -d
```

---

## 🔍 Exploration & Debugging (Database)

### 1. Mode H2 (Développement Local Rapide)
Si vous lancez l'application sans profil particulier (H2 par défaut), la base est un fichier local `./database`.
- **Console H2** : `http://localhost:8081/h2-console`
- **JDBC URL** : `jdbc:h2:file:./database`
- **User** : `sa` | **Password** : *(vide)*

### 2. Mode PostgreSQL (Docker / Soutenance)
Si vous utilisez le profil `postgres` :
- **Accès DB** : Utilisez un client (pgAdmin, Dbeaver) sur `localhost:5432`.
- **Identifiants** : User: `enicar` | Password: `enicar2026` | DB: `enicar_db`.

---

## 🔑 Scénarios de Test & Comptes Démo
La base est pré-peuplée avec des données cohérentes (`DatabaseSeeder.java`). 
**Mot de passe universel :** `enicarDemo2026!`

| Rôle | Nom | Email | Scénario de Test |
| :--- | :--- | :--- | :--- |
| **Direction** | Admin | `direction@enicar.ucar.tn` | Services administratifs, vue globale. |
| **Enseignant** | Faouzi | `faouzi.jaidi@enicar.ucar.tn` | Publication officielle, gestion départements. |
| **Étudiant (L)** | Jerbi | `mohamed.jerbi@enicar.ucar.tn` | Leader, Feed interactif, messagerie. |
| **Étudiant (F)** | Babou | `mohamed.babou@enicar.ucar.tn` | Social Network, Feed, Messagerie STOMP. |
| **Étudiant (P)** | Abidi | `mohamed-dhia.abidi@enicar.ucar.tn` | Réseau Pro, Mentorat, Offres d'emploi. |
| **Alumni** | Vermeg | `amine.khelifi@alumni.vermeg.tn` | Recrutement, lien Etudiant-Alumni. |

---

## 👥 Meet The Core Team (INFO2 Groupe C)

The ideation, architecture, and coding responsibilities are equally divided across a professional three-man development team:

* **Mohamed Jerbi (Lead Architect / Backend Engineer):** <br> Global Software Architecture, Database Modeling, Security & Authentication (JWT), and Administrative Modules.
* **Mohamed Babou (Frontend Engineer):** <br> UX/UI Architecture & Engineering of the **Internal Social Network** (Feed algorithms, Group handling, Real-time STOMP Messaging).
* **Mohamed Dhia Islem Abidi (Full-Stack Engineer):** <br> Engineering of the **Professional Network** (Job/Internship workflows, Algorithmic Mentorship pairing, Advanced User Profiling).

---

## 📄 License & Contributing
Tasks, Bug Tracking, and Feature Specifications are intensely managed via the **Issues** and **Projects** boards associated with this GitHub repository. Contributions must align with the `docs/design/cahier_des_charges.md`.

---

## Architectural Quality Strategy

**Technology Stack Additions**
To comply with the strict development guidelines, the stack has natively integrated:
*   **SonarQube** (with JaCoCo) for systemic code smells analysis.
*   **AspectJ (AOP)** for decoupled lifecycle tracing.
*   **JoinFaces/JSF** strictly isolated for server-side administration rendering.

**1. AOP Strategy (Aspect-Oriented Programming)**
A cross-cutting concern layer handles Service operations. The `LoggingAspect.java` utilizes `@Before` and `@AfterReturning` advice via pointcuts (`execution(* tn.enicar.enicarconnect.service.*.*(..))`). This safely extracts trace analytics and performance logging without polluting business logic.

**2. Testing Strategy**
Testing covers the functional pyramid:
*   **Web Layer:** Verified via `MockMvc` evaluating standard API responses on actual Controllers (e.g., `AuthControllerTest`).
*   **Business Layer:** Isolated via Mockito mocks injecting repository stubs (e.g., `UserServiceTest`).
*   **Data Access Layer:** Integrated inside an h2 execution context evaluating real generated JPA schemas via `@DataJpaTest` (e.g., `UserRepositoryTest`).

**3. Code Quality (SonarQube) & Logging**
We enforce gates natively using the `sonar-maven-plugin`. The `sonar-project.properties` configuration enforces quality gates excluding non-business classes (like DTOs). In addition, centralized SLF4J/Logback logs rotate daily via `logback-spring.xml`.

**4. JSF Architecture Constraint Adaptation**
Given that the ENICAR application consumes REST pipelines via an Angular SPA, JSF was heavily conflicting. We met the requirement by deploying a lightweight, internal server-side isolated "Monitoring / Admin Dash" using JoinFaces rendered via backing beans natively managed by `@Named` IoC containers.
