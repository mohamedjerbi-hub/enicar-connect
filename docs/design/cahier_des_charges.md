# Cahier des Charges — ENICAR Connect

**Projet :** Plateforme Web Communautaire de l'ENI Carthage  
**Équipe de réalisation :** Mohamed Jerbi, Mohamed Babou, Mohamed Dhia Al Islam Abidi (Classe : 2ème Info Groupe C)

---

## 1. Présentation du projet
### 1.1 Contexte
L’École Nationale d’Ingénieurs de Carthage (ENI Carthage) souhaite développer une plateforme web centralisée destinée à l’ensemble de sa communauté. Actuellement, la communication et la gestion administrative sont dispersées entre plusieurs canaux (WhatsApp, Facebook, procédures papier), ce qui nuit à l’efficacité.

### 1.2 Objectifs généraux
- Centraliser toutes les communications et services de l’école.
- Remplacer les canaux de communication dispersés.
- Digitaliser les procédures administratives (actuellement sur papier).
- Renforcer les liens entre étudiants, enseignants, personnel, direction et alumni.
- Faciliter l’insertion professionnelle des étudiants.
- Optimiser la gestion quotidienne de l’école.

### 1.3 Périmètre du projet
La plateforme comprend 3 modules principaux :
1. **Module Réseau Social Interne**
2. **Module Réseau Professionnel**
3. **Module Services Utiles**

---

## 2. Utilisateurs Cibles
- **Étudiants :** Inscrits à ENI Carthage (Toutes filières/niveaux).
- **Enseignants :** Permanents, vacataires, intervenants externes.
- **Personnel administratif :** Services administratifs, technique, maintenance.
- **Direction :** Directeur, directeurs des études, responsables de départements.
- **Alumni :** Anciens diplômés.

*(Un système de gestion des rôles (RBAC) contrôle l'accès aux fonctionnalités.)*

---

## 3. Module Réseau Social Interne
### 3.1 Fil d’actualités
- **Publications :** Textes, médias (images/vidéos), liens, mentions, hashtags. Options de visibilité (public/privé).
- **Interactions :** Likes/réactions, commentaires, partages, signalement.
- **Modération :** Validation de contenu sensible, suppression.

### 3.2 Groupes
Groupes par filière, promotion, clubs, projets ou thèmes.
- Espaces dédiés avec fil d'actualité, messagerie, partage de ressources.

### 3.3 Événements
Création d'événements (académiques, culturels, sportifs).
- Gestion des participations (Participe/Intéressé), rappels, galerie photo.

### 3.4 Messagerie Interne
Conversations 1-to-1 et groupes, partage de fichiers, notifications temps réel.

---

## 4. Module Réseau Professionnel
### 4.1 Profils Utilisateurs (Type CV)
Parcours académique, expériences professionnelles (alumni), compétences, centres d'intérêt.
### 4.2 Connexions & Recommandations
Réseautage, suggestions de contacts, validation de compétences.
### 4.3 Opportunités Professionnelles
- **Offres :** Stage, PFE, Emploi.
- **Gestion :** Tableaux de bord de recrutement, candidature en ligne, suivi de statut.
### 4.4 Programme de Mentorat
Matching entre étudiants (mentorés) et alumni/enseignants (mentors), suivi des objectifs.

---

## 5. Module Services Utiles
### 5.1 Administration Numérique (RBAC)
Gestion centralisée des rôles (étudiants, enseignants, etc.) et contrôle d'accès sécurisé aux différentes ressources de la plateforme.
### 5.2 Demandes Administratives Automatisées
Attestations diverses (scolarité, réussite, stage), génération automatique de documents PDF, dématérialisation des workflows de validation.
### 5.3 Consultation des Notes & Réclamations
Visualisation sécurisée des relevés, workflow de réclamation numérisé.

---

## 6. Spécifications Techniques

### 6.1 Architecture
- **Frontend :** PWA, Responsive (Angular 17+).
- **Backend :** Architecture backend monolithique d'entreprise (Spring Boot 3).
- **Bases de données :** SGBD Relationnel PostgreSQL (production) et base en mémoire H2 (développement).

### 6.2 Sécurité
- Authentification JWT, RBAC, HTTPS/TLS. Protection parémétrée (SQL Injection, XSS).

### 6.3 Performance
- Temps de réponse < 500ms, chargement SPA < 3s, scalabilité horizontale, CDN.

---

## 7. Indicateurs de Succès (KPIs)
- **Disponibilité :** > 99.5%
- **Adoption :** > 80% des cibles.
- **Absorbtion :** Réduction de 50% du temps de traitement administratif.  
- **Usage Métier :** Désertion de Facebook/WhatsApp pour la communication interne de l'école.
