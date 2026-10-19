# Scénarios de tests complets — ENICAR Connect

Ce document détaille les parcours de tests pour valider l'ensemble des fonctionnalités de la plateforme ENICAR Connect.

## 1. AUTHENTIFICATION ET PROFILS

### Connexion Étudiant
- **Identifiant** : `m.jerbi@enicar.ucar.tn`
- **Mot de passe** : `Etud@1234`
- **Vérification** : Le tableau de bord affiche les publications de la filière "Informatique".

### Connexion Enseignant
- **Identifiant** : `f.jaidi@enicar.ucar.tn`
- **Mot de passe** : `Prof@1234`
- **Vérification** : Accès aux groupes "Corps Enseignant" et "Informatique".

### Connexion Direction
- **Identifiant** : `direction@enicar.ucar.tn`
- **Mot de passe** : `Admin@1234`
- **Vérification** : Accès aux statistiques globales.

---

## 2. RÉSEAU SOCIAL ET GROUPES

### Publication (Feed)
1. Se connecter avec `m.jerbi@enicar.ucar.tn`.
2. Créer un post : "Nouveau projet Angular en cours ! #Angular #ENICAR".
3. Se connecter avec `ines.khelifi@enicar.ucar.tn`.
4. Liker le post de Jerbi et ajouter un commentaire : "Super projet !".

### Navigation par Groupes
1. Aller dans l'onglet "Groupes".
2. Sélectionner "Informatique".
3. Vérifier la présence du post de Faouzi Jaidi sur les TPs Sécurité.

---

## 3. MESSAGERIE INSTANTANÉE (WebSockets)

### Discussion de classe
1. Ouvrir deux navigateurs (ou une fenêtre privée).
2. Browser A : Connecté avec `m.jerbi@enicar.ucar.tn`.
3. Browser B : Connecté avec `ines.khelifi@enicar.ucar.tn`.
4. Ouvrir le groupe "2ème Info - Groupe C".
5. Envoyer un message depuis A, vérifier la réception instantanée sur B.

---

## 4. MATCHING ET OFFRES D'EMPLOI

### Recherche d'offres
1. Se connecter avec `m.jerbi@enicar.ucar.tn`.
2. Aller dans la section "Offres".
3. Cliquer sur l'offre "Vermeg Tunisie".
4. **Vérification du Matching** : Le score doit être de **100%** (Java, Spring, Angular, SQL).

### Comparaison Matching
1. Se connecter avec `m.abidi@enicar.ucar.tn`.
2. Vérifier l'offre Vermeg : le score doit être de **75%** (manque la compétence Java).
3. Se connecter avec `m.babou@enicar.ucar.tn`.
4. Vérifier l'offre Vermeg : le score doit être de **0%** (aucune compétence commune).

---

## 5. MENTORAT

### Demande de mentorat
1. Se connecter avec `m.jerbi@enicar.ucar.tn`.
2. Aller dans "Alumni" et trouver `Hatem Bouaziz`.
3. Cliquer sur "Demander un mentorat".
4. Se connecter avec `hatem.bouaziz@gmail.com` (password: `Alumni@1234`).
5. Vérifier la réception de la demande dans le tableau de bord mentorat.

---

## 6. ADMINISTRATION

### Dashboard Admin
1. Accéder à l'URL : `http://localhost:8081/admin/dashboard.xhtml`.
2. Se connecter avec `admin@enicar.ucar.tn` (password: `Admin@1234`).
3. Vérifier les compteurs :
    - Utilisateurs : 21
    - Offres : 6
    - Groupes : ~10
