# ENICAR Connect 🏛️

> **Plateforme web centralisée de la communauté de l'École Nationale d'Ingénieurs de Carthage**

*Projet de Développement Web Avancé - Spring Boot & Angular*

---

## 📋 PRÉSENTATION DU PROJET

L’École Nationale d’Ingénieurs de Carthage (ENI Carthage) a développé cette plateforme web centralisée destinée à l’ensemble de sa communauté. L'objectif est de remplacer les canaux de communication dispersés, de digitaliser les processus, et de renforcer les liens professionnels.

La plateforme comprend 3 modules principaux :
1. **Module Réseau Social Interne :** Fil d'actualité, création et gestion de groupes, gestion d'événements, messagerie instantanée.
2. **Module Réseau Professionnel :** Profils riches (CV), connexions étudiantes/alumni, offres de stages et d'emploi, mentorat.
3. **Module Services Utiles & Administration :** Outils d'administration, rôles, permissions et digitalisations de documents.

---

## 👥 ÉQUIPE MOTEUR — INFO2 Groupe C

La conception et le développement de cette plateforme ont été répartis équitablement pour couvrir une architecture professionnelle :

* **Mohamed Jerbi (Chef de projet) :** Architecture globale logicielle et base de données, infrastructure de Sécurité (JWT), et Modération & Administration globale.
* **Mohamed Babou (Développeur) :** Architecture et Frontend du **Réseau Social Interne** (Posts, Groupes, Événements, Messagerie temps réel).
* **Mohamed Dhia Islem Abidi (Développeur) :** Architecture et Frontend du **Réseau Professionnel** (Opportunités de stage/emploi, Mentorat, Profils Avancés).

---

## 🛠️ ARCHITECTURE ET DÉMARRAGE

### Stack Technique
* **Frontend** : Angular 15+ (TypeScript, RxJS)
* **Backend** : Spring Boot 3 (Java 17, Spring Security, JWT, Data JPA)
* **Base de données** : MySQL 8.x + H2 DB (en développement)
* **Temps réel** : WebSockets (STOMP)

### Comment lancer l'application en développement

**1. Lancement du Frontend (Angular) :**
Ouvrez un terminal et exécutez les commandes suivantes :
```bash
cd enicar-connect-frontend
npm install
# Construire et lancer le serveur de développement local
npm start
# L'application sera accessible sur http://localhost:4200
```

**2. Lancement du Backend (Spring Boot) :**
Ouvrez un autre terminal. Assurez-vous d'avoir Java 17 installé et la variable `JAVA_HOME` configurée.
```bash
cd enicar-connect-backend
..\.tools\apache-maven-3.9.9\bin\mvn.cmd clean compile
..\.tools\apache-maven-3.9.9\bin\mvn.cmd spring-boot:run
# L'API tourne sur http://localhost:8081 (server.port dans application.properties)
```

Si Maven est installé globalement sur votre machine, vous pouvez utiliser à la place :
```bash
mvn clean compile
mvn spring-boot:run
```

> **Note :** Les tâches (Features / Bugs) de développement actives sont directement suivies via l'onglet **Issues** de ce dépôt GitHub et réparties entre les membres de l'équipe.
