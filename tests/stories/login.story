Narrative:
  In order to access the ENICAR Connect platform securely
  As a registered user of ENICarthage
  I want to authenticate and be authorized according to my role

Scenario: Connexion valide redirige vers le feed

Given un utilisateur avec l'email m.babou@enicar.ucar.tn et le mot de passe Etud@1234
When il soumet le formulaire de connexion
Then il est redirigé vers la page /feed
And un token JWT valide est présent dans la réponse
And le token contient le rôle STUDENT

Scenario: Connexion avec mauvais mot de passe est rejetée

Given un utilisateur avec l'email m.babou@enicar.ucar.tn
When il soumet le formulaire avec le mot de passe incorrectMotDePasse
Then il reste sur la page de login
And un message d'erreur d'authentification est affiché
And aucun token JWT n'est retourné

Scenario: Étudiant ne peut pas accéder aux fonctionnalités administrateur

Given un utilisateur connecté avec le rôle STUDENT
When il tente d'accéder à l'URL /api/users réservée aux administrateurs
Then le système retourne le code HTTP 403 Forbidden
And aucune donnée utilisateur n'est divulguée

Scenario: Accès sans token JWT est rejeté

Given un utilisateur non authentifié
When il tente d'accéder à /api/posts sans token dans le header Authorization
Then le système retourne le code HTTP 401 Unauthorized
And le message indique que l'authentification est requise

Scenario: Injection SQL dans le formulaire de login est bloquée

Given un attaquant qui tente une injection SQL
When il soumet l'email ' OR '1'='1 et n'importe quel mot de passe
Then le système retourne le code HTTP 401 Unauthorized
And aucune donnée n'est compromise
