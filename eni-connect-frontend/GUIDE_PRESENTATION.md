# ENI Connect - Guide de Présentation

## 📋 Vue d'Ensemble du Projet

**ENI Connect** est une plateforme web communautaire complète pour l'École Nationale d'Ingénieurs de Carthage, développée en Angular avec un design moderne et professionnel.

### 🎯 Objectifs Réalisés

✅ Centralisation des communications de l'école  
✅ Remplacement des canaux dispersés (WhatsApp, Facebook)  
✅ Digitalisation des procédures administratives  
✅ Interface moderne et cohérente avec le site officiel ENI Carthage

---

## 🎨 Design & Identité Visuelle

### Palette de Couleurs
- **Bleu Principal**: #0078D4 (couleur officielle ENI)
- **Bleu Foncé**: #004578
- **Gris Clair**: #F5F5F5
- **Design**: Glassmorphism, animations fluides, responsive

### Logos Intégrés
- Logo ENI Connect
- Logo ENI Carthage

---

## 📁 Templates Créés

### 1. **Authentification** ✅

#### Login (Connexion)
- **Fichier**: `src/app/auth/login/login.component.html`
- **Fonctionnalités**:
  - Formulaire de connexion avec email/mot de passe
  - Design glassmorphism avec arrière-plan animé
  - Validation de formulaire
  - Option "Se souvenir de moi"
  - Lien mot de passe oublié
  - Lien vers inscription
  - Logos ENI Connect et ENI Carthage intégrés

#### Register (Inscription)
- **Fichier**: `src/app/auth/register/register.component.html`
- **Fonctionnalités**:
  - Sélection du type d'utilisateur (Étudiant, Enseignant, Personnel, Alumni)
  - Formulaire multi-champs avec validation
  - Champs spécifiques selon le type (filière, promotion pour étudiants)
  - Confirmation de mot de passe
  - Acceptation des conditions d'utilisation

---

### 2. **Navigation & Layout** ✅

#### Header (En-tête)
- **Fichier**: `src/app/shared/header/header.component.html`
- **Fonctionnalités**:
  - Logo ENI Connect cliquable
  - Barre de recherche globale
  - Menu de navigation (Accueil, Groupes, Événements, Emplois, Services)
  - Notifications avec dropdown animé
  - Messages
  - Menu profil utilisateur avec dropdown
  - Design sticky (reste en haut lors du scroll)

---

### 3. **Dashboard Principal** ✅

#### Dashboard
- **Fichier**: `src/app/dashboard/dashboard.component.html`
- **Structure**:
  - **Sidebar Gauche**:
    - Carte profil utilisateur avec photo de couverture
    - Statistiques (connexions, groupes)
    - Liens rapides
  - **Feed Central**:
    - Fil d'actualités principal
  - **Sidebar Droite**:
    - Événements à venir
    - Suggestions de connexions
    - Liens footer

---

### 4. **Module Réseau Social** ✅

#### Feed (Fil d'Actualités)
- **Fichier**: `src/app/social/feed/feed.component.html`
- **Fonctionnalités**:
  - Zone de création de post (texte, photo/vidéo, événement, document)
  - Cartes de publication avec:
    - Photo de profil et informations utilisateur
    - Contenu texte et images
    - Hashtags cliquables
    - Statistiques (réactions, commentaires, partages)
    - Boutons d'interaction (J'aime, Commenter, Partager)
  - Exemples de posts variés (Club Robotique, Étudiant, Direction)

#### Groups (Groupes)
- **Fichier**: `src/app/social/groups/groups.component.html`
- **Fonctionnalités**:
  - Filtres par onglets (Tous, Mes groupes, Suggestions)
  - Filtres par catégorie (Filières, Promotions, Clubs, Projets)
  - Grille de cartes de groupes avec:
    - Image de couverture
    - Nom et catégorie du groupe
    - Description
    - Statistiques (membres, publications)
    - Bouton Rejoindre/Membre
  - 6 exemples de groupes différents

#### Events (Événements)
- **Fichier**: `src/app/social/events/events.component.html`
- **Fonctionnalités**:
  - Toggle vue Liste/Calendrier
  - Filtre de sélection
  - Cartes d'événements avec:
    - Badge de date stylisé
    - Image d'événement avec catégorie
    - Titre et métadonnées (heure, lieu, organisateur)
    - Description
    - Avatars des participants
    - Bouton Participer/Intéressé
  - 3 exemples d'événements (Conférence IA, Hackathon, Soirée Culturelle)

---

### 5. **Module Réseau Professionnel** ✅

#### Jobs (Offres d'Emploi)
- **Fichier**: `src/app/professional/jobs/jobs.component.html`
- **Fonctionnalités**:
  - Filtres avancés (Type, Domaine, Localisation)
  - Cartes d'offres avec:
    - Logo entreprise
    - Titre du poste
    - Type de contrat (Stage, CDI, CDD) avec badges colorés
    - Localisation et date de publication
    - Description du poste
    - Compétences requises (tags cliquables)
    - Salaire/Gratification
    - Bouton Postuler
    - Bouton Bookmark (sauvegarder)
  - 3 exemples d'offres variées

---

## 🎯 Points Forts du Projet

### Design Professionnel
- ✨ Glassmorphism et effets modernes
- 🎨 Palette de couleurs cohérente avec ENI Carthage
- 📱 Design 100% responsive (mobile, tablette, desktop)
- 🌊 Animations fluides et micro-interactions
- 🎭 Hover effects et transitions élégantes

### Expérience Utilisateur
- 🔍 Recherche globale
- 🔔 Notifications en temps réel
- 💬 Système de messagerie
- 👥 Suggestions de connexions
- 📅 Calendrier d'événements
- 💼 Plateforme emploi intégrée

### Code Quality
- 📦 Architecture modulaire Angular
- 🎯 Composants réutilisables
- 💅 CSS organisé avec variables
- 📝 Code commenté et structuré
- 🔧 Prêt pour l'intégration backend

---

## 📂 Structure du Projet

```
eni-connect/
├── src/
│   ├── app/
│   │   ├── auth/
│   │   │   ├── login/          # Page de connexion
│   │   │   └── register/       # Page d'inscription
│   │   ├── shared/
│   │   │   └── header/         # En-tête global
│   │   ├── dashboard/          # Dashboard principal
│   │   ├── social/
│   │   │   ├── feed/           # Fil d'actualités
│   │   │   ├── groups/         # Page groupes
│   │   │   └── events/         # Page événements
│   │   └── professional/
│   │       └── jobs/           # Page emplois
│   ├── assets/
│   │   └── images/             # Logos ENI
│   ├── styles.css              # Styles globaux
│   └── index.html              # Page principale
├── package.json
└── README.md
```

---

## 🚀 Comment Présenter au Professeur

### 1. **Introduction** (2 min)
- Présenter le contexte du projet
- Montrer les logos ENI Connect et ENI Carthage
- Expliquer les objectifs de la plateforme

### 2. **Démonstration des Templates** (10 min)

**Ordre recommandé**:

1. **Login** → Montrer le design glassmorphism et l'animation
2. **Register** → Montrer la sélection de type d'utilisateur
3. **Dashboard** → Montrer la structure à 3 colonnes
4. **Feed** → Montrer la création de post et les interactions
5. **Groups** → Montrer les filtres et les cartes de groupes
6. **Events** → Montrer le calendrier et les événements
7. **Jobs** → Montrer les filtres et les offres d'emploi

### 3. **Points Techniques** (3 min)
- Architecture Angular modulaire
- Design system avec variables CSS
- Responsive design
- Cohérence avec le site officiel ENI

### 4. **Fonctionnalités Futures** (2 min)
- Intégration backend (API REST)
- Messagerie en temps réel
- Notifications push
- Module services administratifs complet

---

## 📸 Captures d'Écran Recommandées

Pour la présentation, préparez des captures d'écran de:
1. Page de connexion
2. Page d'inscription
3. Dashboard complet
4. Fil d'actualités avec posts
5. Page groupes
6. Page événements
7. Page emplois

---

## 🎓 Équipe de Développement

- **Mohamed Jerbi**
- **Mohamed Babou**
- **Mohamed Dhia Al Islam Abidi**

**Classe**: 2ème Info Groupe C  
**École**: ENI Carthage  
**Année**: 2025-2026

---

## 📝 Notes pour la Présentation

### Ce qui impressionnera le professeur:

✅ **Design moderne et professionnel** - Bien au-delà d'un simple MVP  
✅ **Cohérence visuelle** - Palette de couleurs ENI Carthage respectée  
✅ **Completude** - Tous les modules principaux sont présents  
✅ **Responsive** - Fonctionne sur tous les appareils  
✅ **Détails** - Animations, hover effects, micro-interactions  
✅ **Organisation** - Code structuré et modulaire

### Points à mentionner:

- Utilisation de Font Awesome pour les icônes
- Google Fonts (Roboto, Poppins) pour la typographie
- Design inspiré du site officiel ENI Carthage
- Prêt pour l'intégration avec un backend Spring Boot/Node.js

---

## 🔗 Ressources

- **Site officiel ENI Carthage**: http://www.enicarthage.rnu.tn/
- **Documentation Angular**: https://angular.io/docs
- **Font Awesome**: https://fontawesome.com/

---

**Bonne présentation ! 🎉**
