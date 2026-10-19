package tn.enicar.enicarconnect.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import tn.enicar.enicarconnect.model.*;
import tn.enicar.enicarconnect.repository.*;
import tn.enicar.enicarconnect.service.AuthService;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Données de démonstration tunisiennes réalistes (profil {@code postgres} hors
 * {@code test}).
 * Comptes : voir {@code docs/scenario-tests-enicar-connect.md}.
 */
@Component
@Profile("postgres & !test")
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

        private static final String PWD_ADMIN = "Admin@1234";
        private static final String PWD_PROF = "Prof@1234";
        private static final String PWD_ETUD = "Etud@1234";
        private static final String PWD_ALUMNI = "Alumni@1234";

        private static final Pattern HASHTAG = Pattern.compile("#(\\w+)");

        private final UserRepository userRepository;
        private final GroupRepository groupRepository;
        private final GroupMemberRepository groupMemberRepository;
        private final PostRepository postRepository;
        private final CommentRepository commentRepository;
        private final PostLikeRepository postLikeRepository;
        private final MessageRepository messageRepository;
        private final JobOfferRepository jobOfferRepository;
        private final MentorshipRequestRepository mentorshipRequestRepository;
        private final EducationRepository educationRepository;
        private final ExperienceRepository experienceRepository;
        private final ResourceFileRepository resourceFileRepository;
        private final EventRepository eventRepository;
        private final PasswordEncoder passwordEncoder;
        private final TransactionTemplate transactionTemplate;
        private final AuthService authService;

        public DatabaseSeeder(
                        UserRepository userRepository,
                        GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository,
                        PostRepository postRepository,
                        CommentRepository commentRepository,
                        PostLikeRepository postLikeRepository,
                        MessageRepository messageRepository,
                        JobOfferRepository jobOfferRepository,
                        MentorshipRequestRepository mentorshipRequestRepository,
                        EducationRepository educationRepository,
                        ExperienceRepository experienceRepository,
                        ResourceFileRepository resourceFileRepository,
                        EventRepository eventRepository,
                        PasswordEncoder passwordEncoder,
                        PlatformTransactionManager platformTransactionManager,
                        AuthService authService) {
                this.userRepository = userRepository;
                this.groupRepository = groupRepository;
                this.groupMemberRepository = groupMemberRepository;
                this.postRepository = postRepository;
                this.commentRepository = commentRepository;
                this.postLikeRepository = postLikeRepository;
                this.messageRepository = messageRepository;
                this.jobOfferRepository = jobOfferRepository;
                this.mentorshipRequestRepository = mentorshipRequestRepository;
                this.educationRepository = educationRepository;
                this.experienceRepository = experienceRepository;
                this.resourceFileRepository = resourceFileRepository;
                this.eventRepository = eventRepository;
                this.passwordEncoder = passwordEncoder;
                this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
                this.authService = authService;
        }

        @Override
        public void run(String... args) {
                if (userRepository.count() > 0) {
                        log.info("Base PostgreSQL déjà peuplée ({} utilisateur(s)), seed ignoré.",
                                        userRepository.count());
                        return;
                }

                log.info("Seed ENICAR Connect — données tunisiennes (utilisateurs, groupes, messagerie, mentorat, matching)…");
                transactionTemplate.executeWithoutResult(status -> seedAll());
        }

        private void seedAll() {
                // --- Administrateurs (rôles alignés Spring Security : ADMIN_STAFF / DIRECTION)
                // ---
                User adminSami = persistUser(PWD_ADMIN, Role.ADMIN_STAFF, "Sami", "Mansouri", "admin@enicar.ucar.tn",
                                "Responsable informatique ENICAR Connect — support utilisateurs et modération.",
                                null, null, null, skills("Gouvernance SI", "Sécurité", "Support"),
                                "https://i.pravatar.cc/150?u=admin");

                User directionNadia = persistUser(PWD_ADMIN, Role.DIRECTION, "Nadia", "Belhaj",
                                "direction@enicar.ucar.tn",
                                "Direction des services administratifs — coordination pédagogique et partenariats.",
                                null, null, null, skills("Pilotage", "Relations entreprises", "Qualité"),
                                "https://i.pravatar.cc/150?u=nadia");

                // --- Enseignants (Vrais noms ENICAR) ---
                User profJaidi = persistUser(PWD_PROF, Role.TEACHER, "Faouzi", "Jaidi", "f.jaidi@enicar.ucar.tn",
                                "Enseignant-chercheur — Informatique (Sécurité, Réseaux).",
                                "Informatique", null, null, skills("Sécurité", "Réseaux", "Java"),
                                "https://i.pravatar.cc/150?u=jaidi");

                User profBedoui = persistUser(PWD_PROF, Role.TEACHER, "Khaoula", "Bedoui", "k.bedoui@enicar.ucar.tn",
                                "Enseignant-chercheur — Informatique (Web, Intelligence Artificielle).",
                                "Informatique", null, null, skills("Web", "IA", "Python"),
                                "https://i.pravatar.cc/150?u=bedoui");

                User profBenSlimen = persistUser(PWD_PROF, Role.TEACHER, "Iyed", "Ben Slimen",
                                "i.ben_slimen@enicar.ucar.tn",
                                "Enseignant-chercheur — Mécanique (Structures, Matériaux).",
                                "Mécanique", null, null, skills("CAO", "Mécanique du solide", "Simulink"),
                                "https://i.pravatar.cc/150?u=iyed");

                User profYaich = persistUser(PWD_PROF, Role.TEACHER, "Wiem", "Yaich", "w.yaich@enicar.ucar.tn",
                                "Enseignant-chercheur — Mécatronique (Electronique, Asservissements).",
                                "Mécatronique", null, null, skills("MATLAB", "Electronique", "Automatisme"),
                                "https://i.pravatar.cc/150?u=wiem");

                User profGuesmi = persistUser(PWD_PROF, Role.TEACHER, "Rachid", "Guesmi", "r.guesmi@enicar.ucar.tn",
                                "Enseignant-chercheur — Mathématiques appliquées à l'ingénierie.",
                                "Informatique", null, null, skills("Optimisation", "Probabilités", "Data Science"),
                                "https://i.pravatar.cc/150?u=rachid");

                // --- Étudiants (Vrais membres du projet + corrections) ---
                User studJerbi = persistUser(PWD_ETUD, Role.STUDENT, "Mohamed", "Jerbi", "m.jerbi@enicar.ucar.tn",
                                "Étudiant en 2ème Info — Lead Developer ENICAR Connect.",
                                "Informatique", "Promotion 2026", "2ème Info - Groupe C",
                                skills("Java", "Spring Boot", "Angular", "SQL", "Git"),
                                "https://i.pravatar.cc/150?u=m_jerbi");

                User studBabou = persistUser(PWD_ETUD, Role.STUDENT, "Mohamed", "Babou", "m.babou@enicar.ucar.tn",
                                "Étudiant en 2ème Info — Backend & DevOps ENICAR Connect.",
                                "Informatique", "Promotion 2026", "2ème Info - Groupe C",
                                skills("Python", "React", "Docker", "Git", "PostgreSQL"),
                                "https://i.pravatar.cc/150?u=m_babou");

                User studAbidi = persistUser(PWD_ETUD, Role.STUDENT, "Mohamed Dhia Islem", "Abidi",
                                "m.abidi@enicar.ucar.tn",
                                "Étudiant en 2ème Info — Fullstack Developer ENICAR Connect.",
                                "Informatique", "Promotion 2026", "2ème Info - Groupe C",
                                skills("Angular", "Spring Boot", "SQL", "REST API", "Git"),
                                "https://i.pravatar.cc/150?u=islem");

                User studAhmed = persistUser(PWD_ETUD, Role.STUDENT, "Ahmed", "Ben Salah",
                                "ahmed.ben_salah@enicar.ucar.tn",
                                "Étudiant en Informatique — projets web et intégration continue.",
                                "Informatique", "Promotion 2026", "2ème Info - Groupe C",
                                skills("Java", "Spring Boot", "Angular", "SQL", "Git"),
                                "https://i.pravatar.cc/150?u=ahmed");

                User studInes = persistUser(PWD_ETUD, Role.STUDENT, "Inès", "Khelifi", "ines.khelifi@enicar.ucar.tn",
                                "Étudiante en Informatique — backend Python et conteneurisation.",
                                "Informatique", "Promotion 2026", "2ème Info - Groupe C",
                                skills("Python", "Django", "React", "PostgreSQL", "Docker"),
                                "https://i.pravatar.cc/150?u=ines");

                User studYoussef = persistUser(PWD_ETUD, Role.STUDENT, "Youssef", "Hamdi",
                                "youssef.hamdi@enicar.ucar.tn",
                                "Étudiant en Informatique — APIs et front Angular.",
                                "Informatique", "Promotion 2026", "2ème Info - Groupe A",
                                skills("Java", "Angular", "MySQL", "REST API"), null);

                User studMariem = persistUser(PWD_ETUD, Role.STUDENT, "Mariem", "Sfar", "mariem.sfar@enicar.ucar.tn",
                                "Étudiante en Informatique — DevOps et cloud.",
                                "Informatique", "Promotion 2025", "3ème Info - Groupe B",
                                skills("Spring Boot", "React", "Docker", "Kubernetes", "Git", "SQL"),
                                "https://i.pravatar.cc/150?u=mariem");

                User studAli = persistUser(PWD_ETUD, Role.STUDENT, "Ali", "Jebali", "ali.jebali@enicar.ucar.tn",
                                "Étudiant en Informatique — data science appliquée.",
                                "Informatique", "Promotion 2025", "3ème Info - Groupe B",
                                skills("Python", "Machine Learning", "TensorFlow", "SQL", "Flask"), null);

                User studSarra = persistUser(PWD_ETUD, Role.STUDENT, "Sarra", "Mejri", "sarra.mejri@enicar.ucar.tn",
                                "Étudiante en Informatique — première année cycle ingénieur.",
                                "Informatique", "Promotion 2027", "1ère Info - Groupe A",
                                skills("HTML", "CSS", "JavaScript", "Git"), null);

                User studOmar = persistUser(PWD_ETUD, Role.STUDENT, "Omar", "Zouari", "omar.zouari@enicar.ucar.tn",
                                "Étudiant en Mécanique — conception et dessin de projet.",
                                "Mécanique", "Promotion 2026", "2ème Méca - Groupe A",
                                skills("AutoCAD", "SolidWorks", "CATIA", "Excel", "MATLAB"), null);

                User studNour = persistUser(PWD_ETUD, Role.STUDENT, "Nour", "Baccar", "nour.baccar@enicar.ucar.tn",
                                "Étudiante en Mécatronique — systèmes embarqués.",
                                "Mécatronique", "Promotion 2026", "2ème Mécat - Groupe B",
                                skills("MATLAB", "Simulink", "C++", "Arduino", "SolidWorks"), null);

                User studBilel = persistUser(PWD_ETUD, Role.STUDENT, "Bilel", "Farhat", "bilel.farhat@enicar.ucar.tn",
                                "Étudiant en Informatique — stack Java / Angular et déploiement.",
                                "Informatique", "Promotion 2025", "3ème Info - Groupe A",
                                skills("Java", "Spring Boot", "Angular", "Docker", "Git", "PostgreSQL", "REST API"),
                                null);

                User studRania = persistUser(PWD_ETUD, Role.STUDENT, "Rania", "Chaker", "rania.chaker@enicar.ucar.tn",
                                "Étudiante en Informatique — bases Python et web.",
                                "Informatique", "Promotion 2027", "1ère Info - Groupe B",
                                skills("Python", "HTML", "CSS", "Git"), null);

                // --- Alumni ---
                User alumHatem = persistUser(PWD_ALUMNI, Role.ALUMNI, "Hatem", "Bouaziz", "hatem.bouaziz@gmail.com",
                                "Diplômé promo 2020 — Senior Java Developer chez Vermeg Tunisie (Lac 2).",
                                "Génie Informatique", null, null, skills("Java", "Spring Boot", "Microservices"), null);

                User alumLeila = persistUser(PWD_ALUMNI, Role.ALUMNI, "Leila", "Maaroufi", "leila.maaroufi@gmail.com",
                                "Diplômée promo 2019 — Tech Lead Angular chez Telnet Holding (Centre urbain Nord).",
                                "Génie Informatique", null, null,
                                skills("Angular", "TypeScript", "RxJS", "Architecture front"), null);

                User alumMalek = persistUser(PWD_ALUMNI, Role.ALUMNI, "Malek", "Dridi", "malek.dridi@gmail.com",
                                "Diplômé promo 2021 — DevOps Engineer chez Sofrecom Tunisie.",
                                "Génie Informatique", null, null, skills("Docker", "Kubernetes", "CI/CD", "GitLab"),
                                null);

                User alumAnis = persistUser(PWD_ALUMNI, Role.ALUMNI, "Anis", "Haddad", "anis.haddad@gmail.com",
                                "Diplômé promo 2018 — Data Engineer chez BIAT Digital.",
                                "Génie Informatique", null, null, skills("Python", "SQL", "Spark", "ETL"), null);

                List<User> allSeeded = Arrays.asList(
                                adminSami, directionNadia, profJaidi, profBedoui, profBenSlimen, profYaich,
                                profGuesmi,
                                studJerbi, studBabou, studAbidi,
                                studAhmed, studInes, studYoussef, studMariem, studAli, studSarra, studOmar, studNour,
                                studBilel, studRania,
                                alumHatem, alumLeila, alumMalek, alumAnis);

                for (User u : allSeeded) {
                        authService.ensureDefaultGroupsForSeed(u);
                }

                // --- Groupes communautaires (filères, clubs, thématiques) ---
                AppGroup groupeInfo = persistGroup(profJaidi, "Informatique",
                                "Actualités filière Informatique, projets, stages et entraide entre promotions.",
                                GroupType.FILIERE, GroupKind.CUSTOM);
                AppGroup groupeMeca = persistGroup(profBenSlimen, "Mécanique",
                                "Projets structures, chantiers et outils numériques (Mécanique, CAO).",
                                GroupType.FILIERE,
                                GroupKind.CUSTOM);
                AppGroup groupeMecat = persistGroup(profYaich, "Mécatronique",
                                "Automatisme, électronique de puissance et projets embarqués.", GroupType.FILIERE,
                                GroupKind.CUSTOM);
                AppGroup groupeAlumniTn = persistGroup(directionNadia, "Alumni & carrière Tunis",
                                "Réseau des diplômés ENICAR : opportunités en Tunisie (Grand Tunis, Lac, Sfax, Sousse).",
                                GroupType.CLUB, GroupKind.CUSTOM);
                AppGroup groupeStages = persistGroup(alumHatem, "Stages & PFE — Grand Tunis",
                                "Offres de stages, PFE et alternance autour de Tunis, Ariana et Ben Arous. #Tunis #Stages",
                                GroupType.THEMATIC, GroupKind.CUSTOM);
                AppGroup groupeHackathon = persistGroup(studJerbi, "Innovation & hackathons",
                                "Binômes, défis tech et préparation aux compétitions étudiantes.", GroupType.CLUB,
                                GroupKind.CUSTOM);

                for (User t : Arrays.asList(profJaidi, profBedoui, profGuesmi)) {
                        addMembership(groupeInfo, t, MemberRole.MEMBER);
                }
                for (User s : Arrays.asList(studJerbi, studBabou, studAbidi, studAhmed, studInes, studYoussef,
                                studMariem,
                                studAli, studSarra, studBilel, studRania)) {
                        addMembership(groupeInfo, s, MemberRole.MEMBER);
                }
                for (User a : Arrays.asList(alumHatem, alumLeila, alumMalek, alumAnis)) {
                        addMembership(groupeInfo, a, MemberRole.MEMBER);
                }
                addMembership(groupeInfo, adminSami, MemberRole.MEMBER);

                addMembership(groupeMeca, profBenSlimen, MemberRole.MEMBER);
                addMembership(groupeMeca, studOmar, MemberRole.MEMBER);

                addMembership(groupeMecat, profYaich, MemberRole.MEMBER);
                addMembership(groupeMecat, studNour, MemberRole.MEMBER);

                for (User a : Arrays.asList(alumHatem, alumLeila, alumMalek, alumAnis, directionNadia)) {
                        addMembership(groupeAlumniTn, a, MemberRole.MEMBER);
                }
                addMembership(groupeAlumniTn, profJaidi, MemberRole.MEMBER);

                for (User u : Arrays.asList(studJerbi, studBabou, studAbidi, studAhmed, studInes, studMariem, studAli,
                                studBilel, alumHatem, alumMalek)) {
                        addMembership(groupeStages, u, MemberRole.MEMBER);
                }

                addMembership(groupeHackathon, studJerbi, MemberRole.MEMBER);
                addMembership(groupeHackathon, studBabou, MemberRole.MEMBER);
                addMembership(groupeHackathon, studAbidi, MemberRole.MEMBER);
                addMembership(groupeHackathon, studInes, MemberRole.MEMBER);
                addMembership(groupeHackathon, studMariem, MemberRole.MEMBER);
                addMembership(groupeHackathon, alumLeila, MemberRole.MEMBER);

                // --- Publications (feed + filière) ---
                String postJaidi = "Bonjour à tous, les TPs Sécurité Réseaux (Spring Security) sont en ligne sur le LMS. "
                                + "Séance synchrone jeudi 14h — lien Zoom dans l'onglet Annonces. #ENICAR #Security #Java";
                postRepository.save(Post.builder()
                                .author(profJaidi)
                                .visibility(Visibility.PUBLIC)
                                .group(groupeInfo)
                                .body(postJaidi)
                                .hashtags(extractHashtags(postJaidi))
                                .build());

                String postJerbi = "Salut la classe — quelqu'un cherche un binôme pour le hackathon « Smart Campus » à la Cité des Sciences ? "
                                + "Stack prévue : Angular + Spring Boot. #Innovation #Carthage";
                Post postJerbiEntity = postRepository.save(Post.builder()
                                .author(studJerbi)
                                .visibility(Visibility.PUBLIC)
                                .group(groupeInfo)
                                .body(postJerbi)
                                .hashtags(extractHashtags(postJerbi))
                                .build());

                String postHatem = "Retour d'expérience après 5 ans chez Vermeg (Lac 2) : investissez tôt sur les revues de code et les tests d'intégration. "
                                + "Dispo pour un café-projet avec les 3ème année GI. #Alumni #Tunis";
                postRepository.save(Post.builder()
                                .author(alumHatem)
                                .visibility(Visibility.PUBLIC)
                                .group(groupeAlumniTn)
                                .body(postHatem)
                                .hashtags(extractHashtags(postHatem))
                                .build());

                String postOmar = "Besoin d'un modèle type pour note de calcul — normes tunisiennes ou Eurocode ? Merci ! #Meca #Questions";
                Post postOmarEntity = postRepository.save(Post.builder()
                                .author(studOmar)
                                .visibility(Visibility.PUBLIC)
                                .group(groupeMeca)
                                .body(postOmar)
                                .hashtags(extractHashtags(postOmar))
                                .build());

                postLikeRepository.save(PostLike.builder().post(postJerbiEntity).user(studInes).build());
                postLikeRepository.save(PostLike.builder().post(postJerbiEntity).user(studBilel).build());

                commentRepository.save(Comment.builder()
                                .post(postJerbiEntity)
                                .author(studInes)
                                .text("Je suis chaude pour Angular — je te MP sur la messagerie groupe C.")
                                .build());

                commentRepository.save(Comment.builder()
                                .post(postOmarEntity)
                                .author(profBenSlimen)
                                .text("Omar : commence par le guide sur les ouvrages de construction, puis on valide les calculs en TD.")
                                .build());

                // --- Messagerie de groupe (threads réalistes) ---
                AppGroup classe2InfoC = groupRepository.findByNameIgnoreCase("2ème Info - Groupe C")
                                .orElse(groupeInfo);
                saveMessage(classe2InfoC, studJerbi, "Slt — qq'un a le lien Zoom du cours UML ce jeudi ?");
                saveMessage(classe2InfoC, studInes,
                                "Oui, c'est dans LMS > Annexes > visioconférences (même lien que la semaine dernière).");
                saveMessage(classe2InfoC, studJerbi, "Merci Inès — je te dois un café à l'cafétéria B7 😄");

                AppGroup corps = groupRepository.findByNameIgnoreCase("Corps Enseignant").orElse(groupeInfo);
                saveMessage(corps, profBedoui, "Rappel : dépôt notes partiel Web avant vendredi 18h sur Scolarité.");
                saveMessage(corps, profJaidi, "Reçu — je synchronise avec la direction pédagogique.");

                saveMessage(groupeStages, alumMalek,
                                "Sofrecom recrute 3 stagiaires DevOps à Lac 1 — envoyez CV + GitHub sur careers@sofrecom.com.");
                saveMessage(groupeStages, studMariem, "Super, je postule ce soir. Quel niveau Kubernetes attendu ?");
                saveMessage(groupeStages, alumMalek,
                                "Bases solides + un mini-projet CI (GitLab ou GitHub Actions) suffisent pour le test technique.");

                // --- Mentorat (matching humain complémentaire au scoring offres) ---
                mentorshipRequestRepository.save(MentorshipRequest.builder()
                                .mentor(alumHatem)
                                .mentee(studAhmed)
                                .objective("Préparation PFE Java / Spring Boot et bonnes pratiques Vermeg.")
                                .status("PENDING")
                                .build());
                mentorshipRequestRepository.save(MentorshipRequest.builder()
                                .mentor(alumLeila)
                                .mentee(studInes)
                                .objective("Montée en compétence Angular 17+ et design d'API REST.")
                                .status("ACTIVE")
                                .build());
                mentorshipRequestRepository.save(MentorshipRequest.builder()
                                .mentor(profGuesmi)
                                .mentee(studRania)
                                .objective("Renforcement maths / optimisation pour module Data Science.")
                                .status("PENDING")
                                .build());

                // --- Offres d'emploi (matching compétences — entreprises tunisiennes) ---
                jobOfferRepository.save(JobOffer.builder()
                                .title("Ingénieur Full-Stack Java / Angular")
                                .company("Vermeg Tunisie")
                                .location("Les Berges du Lac 2, Tunis")
                                .type("CDI")
                                .description("Équipe produit bancaire : Spring Boot, Angular, PostgreSQL, revue de code et qualité Sonar.")
                                .tags("java,springboot,angular,tunis,cdi")
                                .author(alumHatem)
                                .requiredSkills(requiredSkillsSet("Java", "Spring Boot", "Angular", "SQL"))
                                .build());

                jobOfferRepository.save(JobOffer.builder()
                                .title("Tech Lead Front-End")
                                .company("Telnet Holding")
                                .location("Centre urbain Nord, Tunis")
                                .type("CDI")
                                .description("Encadrement équipe Angular, design system et performance Web.")
                                .tags("angular,typescript,lead,tunis")
                                .author(alumLeila)
                                .requiredSkills(requiredSkillsSet("Angular", "TypeScript", "Git"))
                                .build());

                jobOfferRepository.save(JobOffer.builder()
                                .title("Ingénieur DevOps Junior")
                                .company("Sofrecom Tunisie")
                                .location("Lac 1, Tunis")
                                .type("Stage PFE")
                                .description("Pipelines GitLab CI, Docker, déploiements Kubernetes sur projets télécoms.")
                                .tags("docker,kubernetes,devops,stage")
                                .author(alumMalek)
                                .requiredSkills(requiredSkillsSet("Docker", "Kubernetes", "Git"))
                                .build());

                jobOfferRepository.save(JobOffer.builder()
                                .title("Data Engineer")
                                .company("BIAT Digital")
                                .location("Avenue Habib Bourguiba, Tunis")
                                .type("CDI")
                                .description("Pipelines data, SQL avancé, intégration Spark — environnement agile.")
                                .tags("python,sql,data,tunis")
                                .author(alumAnis)
                                .requiredSkills(requiredSkillsSet("Python", "SQL"))
                                .build());

                jobOfferRepository.save(JobOffer.builder()
                                .title("Stage Ingénieur Méthodes — CAO")
                                .company("STUDI Architecture Tunis")
                                .location("El Menzah, Tunis")
                                .type("Stage")
                                .description("Modélisation CAO, coordination technique chantiers résidentiels Grand Tunis.")
                                .tags("cao,solids,meca,stage")
                                .author(profBenSlimen)
                                .requiredSkills(requiredSkillsSet("CAO", "SolidWorks", "AutoCAD"))
                                .build());

                // --- Dynamisation des Groupes (Membres, Ressources, Evénements) ---
                // Membres pour 2ème Info - Groupe C
                addMembership(classe2InfoC, studBabou, MemberRole.MODERATOR);
                addMembership(classe2InfoC, studAbidi, MemberRole.MEMBER);
                addMembership(classe2InfoC, studMariem, MemberRole.MEMBER);
                addMembership(classe2InfoC, studInes, MemberRole.MEMBER);

                // Ressources
                resourceFileRepository.save(ResourceFile.builder()
                                .title("Support de cours UML - Diagrammes de Séquence")
                                .category("Cours")
                                .icon("fas fa-file-pdf")
                                .fileSize("1.2 MB")
                                .filePath("uml_sequence.pdf")
                                .author(profJaidi)
                                .group(classe2InfoC)
                                .build());

                resourceFileRepository.save(ResourceFile.builder()
                                .title("TD1 Corrigé - Algorithmique Avancée")
                                .category("Exercices")
                                .icon("fas fa-file-word")
                                .fileSize("850 KB")
                                .filePath("td1_algo.docx")
                                .author(profBedoui)
                                .group(classe2InfoC)
                                .build());

                // Evénements
                eventRepository.save(AppEvent.builder()
                                .title("Hackathon Innovation ENICAR")
                                .date("2026-05-15")
                                .time("09:00")
                                .location("Amphi A - ENICAR")
                                .description("Challenge de 24h pour créer des solutions durables.")
                                .category("Innovation")
                                .organizer("Club Innovation")
                                .color("var(--role-prof)")
                                .owner(studJerbi)
                                .group(groupRepository.findByNameIgnoreCase("Innovation & Entrepreneuriat")
                                                .orElse(null))
                                .build());

                eventRepository.save(AppEvent.builder()
                                .title("Réunion Coordination PFE 2026")
                                .date("2026-05-20")
                                .time("14:30")
                                .location("Salle de réunion 1")
                                .description("Session d'information sur les procédures de dépôt de PFE.")
                                .category("Administration")
                                .organizer("Direction")
                                .color("var(--role-direction)")
                                .owner(directionNadia)
                                .group(groupRepository.findByNameIgnoreCase("Stages & PFE").orElse(null))
                                .build());

                jobOfferRepository.save(JobOffer.builder()
                                .title("Développeur Backend Java (microservices)")
                                .company("Orange Tunisie Digital Factory")
                                .location("Ariana Ville")
                                .type("CDI")
                                .description("API REST, Spring, intégration sécurité OAuth2 — projets grand public.")
                                .tags("java,spring,microservices,tunis")
                                .author(alumHatem)
                                .requiredSkills(requiredSkillsSet("Java", "REST API", "Spring Boot", "Git"))
                                .build());

                log.info("=== Seed terminé === users={} groups={} posts={} jobs={} mentorships={}",
                                userRepository.count(),
                                groupRepository.count(),
                                postRepository.count(),
                                jobOfferRepository.count(),
                                mentorshipRequestRepository.count());

                // --- Dynamic Education & Experience Seeding ---
                seedEducationAndExperience(studJerbi, profJaidi, alumHatem, studAbidi);

                log.info("Comptes test : Admins {} | Profs {} | Étudiants {} | Alumni {} — détail : docs/scenario-tests-enicar-connect.md",
                                PWD_ADMIN, PWD_PROF, PWD_ETUD, PWD_ALUMNI);
        }

        private User persistUser(
                        String plainPassword,
                        Role role,
                        String firstName,
                        String lastName,
                        String email,
                        String bio,
                        String department,
                        String level,
                        String className,
                        Set<String> skills,
                        String photoUrl) {

                User u = User.builder()
                                .firstName(firstName)
                                .lastName(lastName)
                                .email(email)
                                .password(passwordEncoder.encode(plainPassword))
                                .bio(bio)
                                .department(department)
                                .level(level)
                                .className(className)
                                .role(role)
                                .skills(new LinkedHashSet<>(skills))
                                .avatarColor(resolveRoleColor(role))
                                .avatarBg(resolveRoleBg(role))
                                .photoUrl(photoUrl)
                                .build();

                u = userRepository.save(u);
                log.info("Utilisateur créé: {} ({})", email, role);
                return u;
        }

        private AppGroup persistGroup(User creator, String name, String description,
                        GroupType type, GroupKind kind) {

                AppGroup g = AppGroup.builder()
                                .name(name)
                                .description(description)
                                .creator(creator)
                                .groupType(type)
                                .kind(kind)
                                .privacy(GroupPrivacy.PUBLIC)
                                .icon(iconForDepartment(name))
                                .iconColor("var(--gold)")
                                .bannerGradient("linear-gradient(135deg,#0A1628,#1a3060)")
                                .build();

                g = groupRepository.save(g);
                groupMemberRepository.save(GroupMember.builder()
                                .group(g)
                                .user(creator)
                                .memberRole(MemberRole.ADMIN)
                                .build());

                log.info("Groupe créé: {}", name);
                return g;
        }

        private void addMembership(AppGroup group, User user, MemberRole role) {
                if (!groupMemberRepository.existsByGroupAndUser(group, user)) {
                        groupMemberRepository.save(GroupMember.builder()
                                        .group(group)
                                        .user(user)
                                        .memberRole(role)
                                        .build());
                }
        }

        private void saveMessage(AppGroup group, User sender, String text) {
                messageRepository.save(Message.builder()
                                .recipientGroup(group)
                                .sender(sender)
                                .content(text)
                                .build());
        }

        private Set<String> requiredSkillsSet(String... skills) {
                return new LinkedHashSet<>(Arrays.asList(skills));
        }

        private Set<String> skills(String... raw) {
                return new LinkedHashSet<>(Arrays.asList(raw));
        }

        private String resolveRoleColor(Role role) {
                return switch (role) {
                        case STUDENT -> "var(--role-student)";
                        case TEACHER -> "var(--role-prof)";
                        case ADMIN_STAFF -> "var(--role-admin)";
                        case DIRECTION -> "var(--role-direction)";
                        case ALUMNI -> "var(--role-alumni)";
                };
        }

        private String resolveRoleBg(Role role) {
                return switch (role) {
                        case STUDENT -> "rgba(99,102,241,.15)";
                        case TEACHER -> "rgba(168,85,247,.15)";
                        case ADMIN_STAFF -> "rgba(34,197,94,.15)";
                        case DIRECTION -> "rgba(234,179,8,.15)";
                        case ALUMNI -> "rgba(249,115,22,.15)";
                };
        }

        private String iconForDepartment(String name) {
                if (name.contains("Informatique")) {
                        return "fas fa-microchip";
                }
                if (name.contains("Civil") || name.contains("GC")) {
                        return "fas fa-drafting-compass";
                }
                if (name.contains("Électrique") || name.contains("Electrique")) {
                        return "fas fa-bolt";
                }
                if (name.contains("Enseignant")) {
                        return "fas fa-chalkboard-teacher";
                }
                if (name.contains("Alumni")) {
                        return "fas fa-user-graduate";
                }
                if (name.contains("Stages") || name.contains("PFE")) {
                        return "fas fa-briefcase";
                }
                if (name.contains("Innovation") || name.contains("hackathon")) {
                        return "fas fa-lightbulb";
                }
                return "fas fa-users";
        }

        private String extractHashtags(String body) {
                if (body == null) {
                        return null;
                }
                LinkedHashSet<String> tags = new LinkedHashSet<>();
                Matcher m = HASHTAG.matcher(body);
                while (m.find()) {
                        tags.add(m.group(1).toLowerCase());
                }
                return tags.isEmpty() ? null : String.join(",", tags);
        }

        private void seedEducationAndExperience(User jerbi, User jaidi, User hatem, User abidi) {
                // Jerbi
                experienceRepository.save(Experience.builder()
                                .user(jerbi)
                                .title("Stagiaire Développeur Web")
                                .company("Carthage Solutions")
                                .period("Juin–Août 2025")
                                .description("Développement Angular + Spring Boot. Pipeline CI/CD GitLab.")
                                .icon("fas fa-code")
                                .build());
                educationRepository.save(Education.builder()
                                .user(jerbi)
                                .degree("Génie Informatique")
                                .school("ENI Carthage")
                                .period("2023–2026")
                                .icon("fas fa-graduation-cap")
                                .build());

                // Prof Jaidi
                experienceRepository.save(Experience.builder()
                                .user(jaidi)
                                .title("Enseignant-Chercheur")
                                .company("ENI Carthage")
                                .period("2015–Présent")
                                .description("Spécialiste en Cybersécurité et Architectures Distribuées.")
                                .icon("fas fa-chalkboard-teacher")
                                .build());

                // Alumni Hatem
                experienceRepository.save(Experience.builder()
                                .user(hatem)
                                .title("Senior Java Developer")
                                .company("Vermeg Tunisie")
                                .period("2020–Présent")
                                .description("Lead technique sur les modules de clearing bancaire.")
                                .icon("fas fa-building")
                                .build());
        }
}
