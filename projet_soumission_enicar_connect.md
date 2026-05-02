# Livrables du Projet : ENICAR Connect (Version Définitive avec Algorithme)

Voici le document final structuré selon vos consignes : la théorie, l'architecture et le contexte vont dans le PowerPoint, et la vidéo est exclusivement réservée à la démonstration pratique de l'application. J'ai également intégré les détails de l'algorithme de matching.

---

## 📄 1. Description Complète et Cahier des Charges (Livrable 4)

**Titre du Projet :** ENICAR Connect
**Public Cible :** La communauté de l'École Nationale d'Ingénieurs de Carthage (ENICAR).

**1. Vision et Objectifs Principaux**
ENICAR Connect est une plateforme numérique innovante conçue spécifiquement pour unifier la communauté de l'ENICAR. Son but est double : 
- **Faciliter une communication transparente et sans friction** entre les différents niveaux hiérarchiques de l'établissement (Étudiants, Professeurs, Administration, Alumni).
- **Créer un pont solide vers le monde professionnel**, en favorisant les interactions publiques et professionnelles.

La plateforme vise notamment à éliminer les malentendus et les conflits souvent liés à la lenteur des démarches administratives traditionnelles, grâce à une messagerie instantanée et efficace.

**2. Problématiques Résolues**
- Manque de communication directe entre les étudiants et l'administration/professeurs, source de conflits ou de retards.
- Difficulté pour les étudiants de trouver des ressources académiques centralisées.
- Déconnexion entre les étudiants actuels et le marché du travail (recherche de stages et d'emplois).

**3. Fonctionnalités Clés**
- **Messagerie Hiérarchique Efficace :** Un système de chat direct permettant aux étudiants de contacter l'administration ou leurs professeurs facilement, évitant ainsi les déplacements physiques inutiles et les conflits de communication.
- **Espace de Partage de Ressources :** Des groupes et un fil d'actualité permettant aux professeurs et aux étudiants de partager des cours, des annonces et du contenu éducatif.
- **Portail d'Opportunités (Jobs/Stages) :** Un espace dédié où les diplômés et les entreprises partenaires peuvent publier des offres d'emploi et de stage, facilitant l'insertion professionnelle.
- **Interactions Publiques et Professionnelles :** Un fil d'actualité (Feed) pour mettre en valeur les projets, les événements de l'école et les réussites professionnelles.
- **Algorithme Intelligents de Matching (Nouveau) :** Un système algorithmique croisant les compétences renseignées par un étudiant avec les compétences exigées par une offre d'emploi. L'algorithme normalise les données, calcule un "Score de Compatibilité" en pourcentage et trie les offres pour proposer les plus pertinentes en premier.

**4. Fonctionnalités Hors Périmètre (Exclues du livrable final)**
Contrairement au cahier des charges initial, les modules suivants **ne sont pas inclus** dans ce projet (afin de se concentrer sur la communication et le volet professionnel) :
- Le système de QR Code.
- La gestion et l'affichage des notes.
- La gestion des absences.
- Le système de réservation des salles.

**5. Architecture Technique**
- **Backend :** API REST robuste développée avec Spring Boot (Java).
- **Frontend :** Application web réactive développée en Angular.
- **Base de données :** MySQL/PostgreSQL.
- **Déploiement / Versioning :** GitHub.

---

## 📊 2. Structure de la Présentation Finale (Livrable 1)
*À créer sur PowerPoint (.pptx). C'est ici que vous expliquez toute la partie théorique et architecturale.*

- **Slide 1 : Page de Garde**
  - Titre : ENICAR Connect.
  - Nom, Classe, Année universitaire, Professeur encadrant.
- **Slide 2 : Le Contexte et les Défis Actuels**
  - Problème de communication entre l'administration, les professeurs et les étudiants (lenteur, conflits).
  - Déconnexion avec le monde professionnel.
- **Slide 3 : La Solution (ENICAR Connect)**
  - Une plateforme dédiée à la communauté ENICAR pour faciliter les interactions publiques et professionnelles.
- **Slide 4 : Les Fonctionnalités Principales**
  - Messagerie directe (résolution de conflits), partage de ressources, recherche d'opportunités (stages/emplois).
- **Slide 5 : Innovation - L'Algorithme de Matching**
  - Explication mathématique/logique de l'algorithme implémenté : *Score = (Compétences Communes / Compétences Requises) × 100*.
  - Comment cet algorithme facilite grandement la recherche d'emploi pour les étudiants en filtrant intelligemment les offres pertinentes.
- **Slide 6 : Structure et Modélisation**
  - Présentation de la base de données (Entités principales : Users, Messages, Jobs, Posts).
- **Slide 7 : Architecture, Frameworks et Déploiement**
  - **Architecture :** Modèle Client/Serveur (Frontend / Backend).
  - **Frameworks :** Spring Boot (Java) pour l'API REST, Angular pour l'interface utilisateur.
  - **Déploiement collaboratif :** Hébergement du code source et gestion des versions via GitHub.
- **Slide 8 : Transition vers la Démonstration**
  - "Place à la démonstration vidéo de notre application."

---

## 🎬 3. Scénario de la Vidéo de Démonstration (Livrable 2)
*Durée : max 6 minutes. La vidéo est 100% consacrée au code, au lancement et à l'utilisation pratique.*

**Étape 1 : Lancement du projet (0:00 - 1:00)**
- Affichez votre éditeur de code.
- Montrez brièvement que le Backend (Spring Boot) tourne dans le terminal.
- Montrez le terminal Frontend avec la commande `ng serve`.
- Ouvrez le navigateur sur `localhost:4200` (ou le port utilisé) pour afficher la page d'accueil de l'application.

**Étape 2 : Inscription et Connexion (1:00 - 1:45)**
- *"Nous voici sur ENICAR Connect. Nous allons nous connecter avec un compte étudiant."*
- Montrez la page de login, entrez les identifiants et accédez à la plateforme.
- Affichez rapidement la page de **Profil** pour montrer l'ajout de compétences techniques (ces compétences serviront pour l'algorithme !).

**Étape 3 : La Messagerie (Prévention des conflits) (1:45 - 3:00)**
- Dirigez-vous vers l'onglet **Messagerie / Chat**.
- *"Voici l'outil de communication direct. Pour éviter la lenteur administrative, un étudiant peut envoyer directement un message à la scolarité ou à un professeur."*
- Simulez l'envoi d'un message : *« Bonjour Madame/Monsieur, j'ai une question concernant mon attestation de présence. »*

**Étape 4 : L'Interaction Publique et les Ressources (3:00 - 4:15)**
- Retournez sur le **Fil d'actualité (Feed)**.
- *"Cet espace permet une interaction publique. Un professeur ou un club peut partager une information à toute la communauté."*
- Rédigez un court post : *« Le support de cours de Java est maintenant disponible ! »*.
- Montrez rapidement la section **Groupes** où les classes peuvent s'organiser.

**Étape 5 : Le Monde Professionnel & Matching Algorithmique (4:15 - 5:30)**
- Allez dans la section **Offres d'emploi (Jobs)**.
- *"Pour faciliter l'insertion professionnelle, les Alumni peuvent poster des offres. Grâce à notre algorithme intégré, les offres sont intelligemment triées avec un score de compatibilité en fonction du profil de l'étudiant !"*
- Montrez une offre avec un pourcentage de "match" et cliquez dessus pour afficher les détails.

**Étape 6 : Clôture de la vidéo (5:30 - 6:00)**
- Déconnectez-vous.
- *"Voici comment ENICAR Connect fluidifie la communication et ouvre des portes professionnelles via l'intelligence algorithmique. Merci."*
