# language: fr
Fonctionnalité: Algorithme de Recommandation d'Offres d'Emploi et PFE
  En tant qu'étudiant de l'ENICAR
  Je veux que le système calcule automatiquement mon taux de compatibilité avec les offres
  Afin de postuler aux offres les plus pertinentes par rapport à mes compétences

  Scénario: Compatibilité parfaite à 100% quand toutes les compétences sont maîtrisées
    Etant donné un étudiant possédant les compétences "Java, Spring Boot, PostgreSQL"
    Et une offre d'emploi intitulée "Ingénieur Java Junior" exigeant "Java, Spring Boot"
    Quand le système calcule le score de matching pour l'étudiant
    Alors le score de compatibilité calculé doit être de 100.0%
    Et les compétences correspondantes doivent être "Java, Spring Boot"

  Scénario: Compatibilité partielle quand une partie des compétences est maîtrisée
    Etant donné un étudiant possédant les compétences "Angular, TypeScript"
    Et une offre d'emploi intitulée "Développeur Fullstack" exigeant "Angular, TypeScript, Node.js, Docker"
    Quand le système calcule le score de matching pour l'étudiant
    Alors le score de compatibilité calculé doit être de 50.0%

  Scénario: Compatibilité nulle quand aucune compétence ne correspond
    Etant donné un étudiant possédant les compétences "C, VHDL"
    Et une offre d'emploi intitulée "Data Scientist" exigeant "Python, TensorFlow"
    Quand le système calcule le score de matching pour l'étudiant
    Alors le score de compatibilité calculé doit être de 0.0%
