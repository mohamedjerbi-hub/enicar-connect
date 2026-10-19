# language: fr
Fonctionnalité: Authentification et Contrôle d'Accès Sécurisé (RBAC)
  En tant qu'utilisateur de la plateforme ENICAR Connect
  Je veux m'authentifier et avoir accès uniquement aux fonctionnalités associées à mon rôle
  Afin de garantir la sécurité et la confidentialité des données académiques

  Scénario: Inscription d'un nouvel étudiant avec assignation de groupe automatique
    Etant donné un nouvel étudiant s'inscrivant avec l'email "nouveau.etudiant@enicar.tn" et le mot de passe "Pass123!"
    Quand l'étudiant valide son formulaire d'inscription
    Alors le compte est créé avec le rôle "STUDENT"
    Et un jeton JWT d'authentification valide est généré

  Scénario: Rejet de connexion en cas de mot de passe erroné
    Etant donné un compte utilisateur existant avec l'email "babou@enicar.tn" et le mot de passe "CorrectPass123"
    Quand l'utilisateur tente de se connecter avec le mot de passe erroné "MauvaisPass"
    Alors la connexion doit être rejetée avec une erreur d'authentification

  Scénario: Contrôle d'accès RBAC empêchant un étudiant d'accéder au module d'administration
    Etant donné un utilisateur connecté avec le rôle "STUDENT"
    Quand il tente d'accéder à l'espace de gestion administrative
    Alors l'accès doit être strictement refusé avec un statut 403 Interdit
