# Scénario de tests ENICAR Connect (données réelles ENICAR)

Ce document décrit les **comptes**, les **mots de passe** et un **parcours de test** bout en bout après un seed PostgreSQL.

## Mots de passe par catégorie

| Catégorie   | Mot de passe |
|------------|--------------|
| Admins     | `Admin@1234` |
| Professeurs | `Prof@1234` |
| Étudiants  | `Etud@1234`  |
| Alumni     | `Alumni@1234`|

## RÉCAPITULATIF FINAL des identifiants

| Rôle       | Prénom Nom                  | Email                          | Mot de passe |
|------------|-----------------------------|--------------------------------|--------------|
| Admin      | Sami Mansouri               | admin@enicar.ucar.tn           | Admin@1234   |
| Admin      | Nadia Belhaj                | direction@enicar.ucar.tn       | Admin@1234   |
| Prof Info  | Faouzi Jaidi                | f.jaidi@enicar.ucar.tn         | Prof@1234    |
| Prof Info  | Khaoula Bedoui              | k.bedoui@enicar.ucar.tn        | Prof@1234    |
| Prof Méca  | Iyed Ben Slimen             | i.ben_slimen@enicar.ucar.tn    | Prof@1234    |
| Prof Mécat | Wiem Yaich                  | w.yaich@enicar.ucar.tn         | Prof@1234    |
| Étudiant   | Mohamed Jerbi               | m.jerbi@enicar.ucar.tn         | Etud@1234    |
| Étudiant   | Mohamed Babou               | m.babou@enicar.ucar.tn         | Etud@1234    |
| Étudiant   | Mohamed Dhia Islem Abidi    | m.abidi@enicar.ucar.tn         | Etud@1234    |
| Étudiant   | Ahmed Ben Salah             | ahmed.ben_salah@enicar.ucar.tn | Etud@1234    |
| Étudiant   | Inès Khelifi                | ines.khelifi@enicar.ucar.tn    | Etud@1234    |
| Étudiant   | Bilel Farhat                | bilel.farhat@enicar.ucar.tn    | Etud@1234    |
| Étudiant   | Mariem Sfar                 | mariem.sfar@enicar.ucar.tn     | Etud@1234    |
| Étudiant   | Ali Jebali                  | ali.jebali@enicar.ucar.tn      | Etud@1234    |
| Étudiant   | Omar Zouari (Méca)          | omar.zouari@enicar.ucar.tn     | Etud@1234    |
| Étudiant   | Nour Baccar (Mécat)         | nour.baccar@enicar.ucar.tn     | Etud@1234    |
| Étudiant   | Sarra Mejri                 | sarra.mejri@enicar.ucar.tn     | Etud@1234    |
| Étudiant   | Rania Chaker                | rania.chaker@enicar.ucar.tn    | Etud@1234    |
| Alumni     | Hatem Bouaziz               | hatem.bouaziz@gmail.com        | Alumni@1234  |
| Alumni     | Leila Maaroufi              | leila.maaroufi@gmail.com       | Alumni@1234  |
| Alumni     | Malek Dridi                 | malek.dridi@gmail.com          | Alumni@1234  |
| Alumni     | Anis Haddad                 | anis.haddad@gmail.com          | Alumni@1234  |

---

## VALIDATION MATCHING — Offre Vermeg PFE (Java, Spring Boot, Angular, SQL)

| Étudiant             | Score attendu | Raison                              |
|----------------------|---------------|-------------------------------------|
| Mohamed Jerbi        | 100%          | Java ✓ Spring Boot ✓ Angular ✓ SQL ✓|
| Mohamed Abidi        | 75%           | Spring Boot ✓ Angular ✓ SQL ✓       |
| Ahmed Ben Salah      | 100%          | Java ✓ Spring Boot ✓ Angular ✓ SQL ✓|
| Bilel Farhat         | 100%          | Java ✓ Spring Boot ✓ Angular ✓ SQL ✓|
| Mohamed Babou        | 0%            | aucune compétence matchée           |
| Inès Khelifi         | 0%            | aucune compétence matchée           |
| Omar Zouari (Méca)   | 0%            | aucune compétence matchée           |
| Nour Baccar (Mécat)  | 0%            | aucune compétence matchée           |

---

## Parcours de test complet (manuel)

1. **Authentification** : Connection avec `m.jerbi@enicar.ucar.tn`.
2. **Groupes** : Vérifier l'appartenance à "Informatique" et "2ème Info - Groupe C".
3. **Feed** : Voir les posts de Faouzi Jaidi et Mohamed Jerbi.
4. **Messagerie** : Thread dans "2ème Info - Groupe C".
5. **Matching** : Vérifier le score de 100% pour Jerbi sur l'offre Vermeg.
