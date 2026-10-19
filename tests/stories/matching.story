Narrative:
  In order to find relevant job opportunities
  As a student of ENICarthage
  I want to see job offers ranked by my compatibility score

Scenario: Étudiant avec compétences Java et SQL voit les meilleures offres en premier

Given un étudiant enregistré avec les compétences Java et SQL
And une offre d'emploi nécessitant Java, SQL et Angular publiée par un Alumni
And une offre d'emploi nécessitant uniquement SOLIDWORKS publiée par un Alumni
When l'étudiant consulte la liste de ses offres avec matching
Then la première offre est celle nécessitant Java, SQL et Angular
And son score de compatibilité est de 67 pourcent
And la deuxième offre a un score de 0 pourcent

Scenario: Étudiant sans compétences voit un score nul pour toutes les offres

Given un étudiant enregistré sans aucune compétence définie
And une offre d'emploi nécessitant Java et Spring Boot publiée par un Alumni
When l'étudiant consulte la liste de ses offres avec matching
Then toutes les offres affichent un score de 0 pourcent

Scenario: Offre sans compétences requises donne un score de 100 pourcent

Given un étudiant enregistré avec les compétences Java et SQL
And une offre d'emploi sans compétences requises publiée par un Alumni
When l'étudiant consulte la liste de ses offres avec matching
Then l'offre affiche un score de 100 pourcent
