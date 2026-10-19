# 📊 Matrice de Traçabilité des Exigences (RTM)
## ENICAR Connect — Sprint Final Testing
**Chef de Projet :** Mohamed Babou | **Équipe :** Babou · Oussema · Mounib

---

## Légende des statuts

| Symbole | Signification |
|:---:|:---|
| ✅ | Test passé (PASSED) |
| ❌ | Test échoué (FAILED) |
| ⚠️ | Résultat partiel ou objectif non atteint |
| 🔄 | En cours |
| ⏳ | Planifié |

---

## Exigences Fonctionnelles (BF) → Cas de Test (TC)

| ID Exigence | Description | Priorité | Type | ID Test | Outil | Statut |
|:---|:---|:---:|:---|:---|:---|:---:|
| **BF-01** | Login avec email + mot de passe → JWT | CRITIQUE | Fonctionnel | TC-AUTH-01 | JUnit + Postman | ✅ |
| **BF-01** | Login invalide → 401 Unauthorized | CRITIQUE | Fonctionnel | TC-AUTH-02 | JUnit + Postman | ✅ |
| **BF-02** | RBAC — Student ne peut pas accéder aux endpoints admin | CRITIQUE | Sécurité | TC-RBAC-01 | JUnit + Postman | ✅ |
| **BF-02** | RBAC — Alumni ne peut pas voir les notes étudiantes | HAUTE | Sécurité | TC-RBAC-02 | JUnit | ✅ |
| **BF-03** | Inscription → création de compte + JWT | CRITIQUE | Fonctionnel | TC-AUTH-05 | JUnit + Postman | ✅ |
| **BF-04** | Gestion du profil étudiant | HAUTE | Fonctionnel | TC-USR-01 | JUnit | ✅ |
| **BF-05** | Gestion du profil alumni | HAUTE | Fonctionnel | TC-USR-02 | JUnit | ✅ |
| **BF-06** | Mentorship — étudiant demande un mentor alumni | HAUTE | Fonctionnel | TC-MENTOR-01 | JUnit | ✅ |
| **BF-06** | Mentorship — duplication bloquée | HAUTE | Fonctionnel | TC-MENTOR-02 | JUnit | ✅ |
| **BF-06** | Mentorship — auto-mentorship bloqué | HAUTE | Fonctionnel | TC-MENTOR-03 | JUnit | ✅ |
| **BF-07** | Connexions — demande d'ami | MOYENNE | Fonctionnel | TC-CONN-01 | JUnit | ✅ |
| **BF-07** | Connexions — accepter/refuser | MOYENNE | Fonctionnel | TC-CONN-02 | JUnit | ✅ |
| **BF-08** | Feed — publication de post | HAUTE | Fonctionnel | TC-POST-01 | JUnit + Postman + E2E | ✅ |
| **BF-08** | Feed — lecture du feed | HAUTE | Fonctionnel | TC-POST-02 | JUnit + Postman | ✅ |
| **BF-08** | Feed — post vide bloqué | MOYENNE | Fonctionnel | TC-POST-03 | JUnit | ✅ |
| **BF-09** | Événements — création d'événement (admin/direction) | HAUTE | Fonctionnel | TC-EVT-01 | JUnit | ✅ |
| **BF-09** | Événements — inscription à un événement | HAUTE | Fonctionnel | TC-EVT-02 | JUnit | ✅ |
| **BF-09** | Événements — student ne peut pas créer | HAUTE | Sécurité | TC-EVT-03 | JUnit | ✅ |
| **BF-10** | Groupes — création de groupe | MOYENNE | Fonctionnel | TC-GRP-01 | JUnit | ✅ |
| **BF-10** | Groupes — rejoindre un groupe | MOYENNE | Fonctionnel | TC-GRP-02 | JUnit | ✅ |
| **BF-11** | Messagerie instantanée — envoi de message | HAUTE | Fonctionnel | TC-MSG-01 | JUnit | ✅ |
| **BF-11** | Messagerie — message hors groupe rejeté | HAUTE | Sécurité | TC-MSG-02 | JUnit | ✅ |
| **BF-12** | Offres d'emploi — publication (alumni) | HAUTE | Fonctionnel | TC-JOB-01 | JUnit + Postman | ✅ |
| **BF-12** | Offres d'emploi — lecture (student) | HAUTE | Fonctionnel | TC-JOB-02 | JUnit | ✅ |
| **BF-14** | Candidature — postuler à une offre | HAUTE | Fonctionnel | TC-JOB-03 | Postman + E2E | ✅ |
| **BF-16** | Matching — algorithme de scoring | CRITIQUE | Algorithme | TC-MATCH-01 | JUnit | ✅ |
| **BF-16** | Matching — score 100% (toutes compétences) | CRITIQUE | Algorithme | TC-MATCH-02 | JUnit | ✅ |
| **BF-16** | Matching — score 50% (partiel) | CRITIQUE | Algorithme | TC-MATCH-03 | JUnit | ✅ |
| **BF-16** | Matching — score 0% (aucune compétence) | CRITIQUE | Algorithme | TC-MATCH-04 | JUnit | ✅ |
| **BF-16** | Matching — tri décroissant | CRITIQUE | Algorithme | TC-MATCH-05 | JUnit + E2E | ✅ |
| **BF-16** | Matching — résultats via API GET /matching | CRITIQUE | Intégration | TC-MATCH-07 | Postman | ✅ |

---

## Exigences Non-Fonctionnelles (BNF) → Cas de Test

| ID Exigence | Description | Critère | ID Test | Outil | Statut |
|:---|:---|:---|:---|:---|:---:|
| **BNF-01** | Performance — temps de réponse API | objectif < 500ms | TC-PERF-01 | JMeter | ⚠️ Partiel : moyenne 144ms, maximum 539ms |
| **BNF-02** | Charge — 50 utilisateurs simultanés | 0% d'erreur | TC-PERF-02 | JMeter | ⚠️ Partiel : 2/250 erreurs (0,80%) |
| **BNF-03** | Sécurité — mot de passe haché (BCrypt) | BCrypt cost=10 | TC-SEC-01 | JUnit + SpotBugs | ✅ |
| **BNF-04** | Sécurité — SQL injection bloquée | 401, aucune donnée | TC-SEC-02 | Postman + OWASP ZAP | ✅ |
| **BNF-05** | Sécurité — XSS bloqué / CSP activé | Contenu échappé | TC-SEC-03 | OWASP ZAP | ✅ |
| **BNF-06** | Sécurité — JWT valide obligatoire | 401 sans token | TC-SEC-04 | JUnit + Postman | ✅ |
| **BNF-07** | Qualité du code — 0 violations Checkstyle | 0 violations | TC-STAT-01 | Checkstyle | ✅ |
| **BNF-08** | Qualité du code — SonarQube Quality Gate | à confirmer par le serveur | TC-STAT-02 | SonarQube | ⏳ |
| **BNF-09** | Couverture de test du périmètre métier | ≥ 70% | TC-COV-01 | JaCoCo | ✅ 71,1% mesurés |

---

## Résumé de Couverture par Type de Test

| Type de Test | Total TC | Passés ✅ | En cours 🔄 | Planifiés ⏳ | % |
|:---|:---:|:---:|:---:|:---:|:---:|
| Tests Unitaires (JUnit 5 + Mockito) | 76 | 76 | 0 | 0 | **100%** |
| Tests d'Intégration (SpringBootTest) | 12 | 12 | 0 | 0 | **100%** |
| Tests BDD / Comportement (Cucumber + JBehave) | 8 | 8 | 0 | 0 | **100%** |
| Tests API — Manuel (Postman) | 8 | 8 | 0 | 0 | **100%** |
| Tests API — Auto (Newman) | 8 | 8 | 0 | 0 | **100%** |
| Tests E2E (Playwright) | 6 | 6 | 0 | 0 | **100%** |
| Tests de Charge (JMeter) | 3 | 0 | 3 | 0 | **Partiel : 0,80% d'erreurs** |
| Tests de Sécurité DAST (OWASP ZAP) | 5 | 5 | 0 | 0 | **100%** |
| Tests Statiques (Checkstyle) | 1 | 1 | 0 | 0 | **100%** |
| Tests Statiques (SpotBugs) | 1 | 0 | 1 | 0 | À confirmer par lecture du rapport |
| Tests Statiques (SonarQube) | 1 | 0 | 0 | 1 | À confirmer *(serveur requis)* |

---

## Anomalies Enregistrées (Bugs Log)

| ID | Description | Sévérité | Statut | Trouvé par | Résolu par |
|:---|:---|:---:|:---:|:---|:---|
| BUG-001 | `GroupMember.toString()` boucle infinie (recursion Lombok) | HAUTE | ✅ Résolu | JUnit | Babou — `@ToString.Exclude` |
| BUG-002 | `AuthControllerTest` bypasse les filtres JWT (`addFilters=false`) | HAUTE | ✅ Résolu | Review de code | Oussema — supprimé |
| BUG-003 | Checkstyle — 15 violations méthodes trop longues | FAIBLE | ✅ Résolu | Checkstyle plugin | Babou — suppressions.xml |
| BUG-004 | Score matching pour offre sans compétences non défini | MOYENNE | ✅ Résolu | JUnit TC-MATCH | Algorithme retourne 100% |
| BUG-005 | OWASP ZAP : En-tête CSP manquant & Sub-Resource Integrity (SRI) | MOYENNE | ✅ Résolu | OWASP ZAP DAST | Babou — CSP SecurityConfig + SRI sha512 |

---

## Traceability Quick Reference — pour la démo

```
BF-16 Matching Algorithm
  └── TC-MATCH-01 ✅  → MatchingServiceTest.givenJobWithAllSkills_thenScore100Percent
  └── TC-MATCH-03 ✅  → MatchingServiceTest.givenPartialSkills_thenScoreIs50Percent
  └── TC-MATCH-07 ✅  → GET /api/matching → 200 OK, sorted array (Postman)
  └── TC-BDD-01  ✅  → matching.feature (Cucumber) / matching.story (JBehave)

BF-01 Authentification
  └── TC-AUTH-01 ✅   → AuthServiceTest + AuthControllerTest (JUnit)
  └── TC-AUTH-01 ✅   → POST /api/auth/login (Postman live demo)
  └── TC-BDD-01  ✅   → authentication.feature / login.story

BF-02 RBAC
  └── TC-RBAC-01 ✅   → SecurityRbacTest.givenStudentJwt_thenForbidden (JUnit)
  └── TC-RBAC-01 ✅   → GET /api/users + STUDENT JWT → 403 (Postman)
  └── TC-RBAC-01 ✅   → Playwright E2E: TC-RBAC-01 (student blocked from admin endpoints)

BNF-01 Performance & Charge (50 users)
  └── TC-PERF-01 & 02 ⚠️ → enicar_connect_load_test.jmx (50 utilisateurs, 250 requêtes, 0,80% d'erreurs)

BNF-04 / BNF-05 Sécurité DAST
  └── TC-SEC-02 & 03  ✅ → OWASP ZAP (BACKUP_SOUTENANCE/zap-report.html) + CSP & SRI fixes
```
