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

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pré-remplit PostgreSQL avec un jeu de données cohérent pour la soutenance ENICAR Connect.
 * Activé uniquement avec le profil {@code postgres} lorsque aucun utilisateur n'existe encore.
 */
@Component
@Profile("postgres")
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private static final String DEMO_PASSWORD = "enicarDemo2026!";
    private static final Pattern HASHTAG = Pattern.compile("#(\\w+)");

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final MessageRepository messageRepository;
    private final JobOfferRepository jobOfferRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;

    public DatabaseSeeder(
            UserRepository userRepository,
            GroupRepository groupRepository,
            GroupMemberRepository groupMemberRepository,
            PostRepository postRepository,
            CommentRepository commentRepository,
            PostLikeRepository postLikeRepository,
            MessageRepository messageRepository,
            JobOfferRepository jobOfferRepository,
            PasswordEncoder passwordEncoder,
            PlatformTransactionManager platformTransactionManager) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.postLikeRepository = postLikeRepository;
        this.messageRepository = messageRepository;
        this.jobOfferRepository = jobOfferRepository;
        this.passwordEncoder = passwordEncoder;
        this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Base PostgreSQL déjà peuplée ({} utilisateur(s)), seed démo ignoré.", userRepository.count());
            return;
        }

        log.info("Démarrage du seed PostgreSQL (ENICAR Connect — démo soutenance)…");
        transactionTemplate.executeWithoutResult(status -> seedAll());
    }

    private void seedAll() {
        String encPwd = passwordEncoder.encode(DEMO_PASSWORD);

        User faouzi = persistUser(encPwd, Role.TEACHER, "Faouzi", "Jaidi", "faouzi.jaidi@enicar.ucar.tn",
                "Professeur à l'ENI Carthage — cours Java / Spring Boot.",
                "Génie Informatique", null, null,
                skills("Java", "Spring Boot", "Architecture", "Microservices"));

        User mohamedJerbi = persistUser(encPwd, Role.STUDENT, "Mohamed", "Jerbi", "mohamed.jerbi@enicar.ucar.tn",
                "Équipe projet ENICAR Connect (front & intégration).",
                "Génie Informatique", "2ème année", "2ème Info Groupe C",
                skills("Angular", "Spring Boot", "UI/UX"));

        User mohamedBabou = persistUser(encPwd, Role.STUDENT, "Mohamed", "Babou", "mohamed.babou@enicar.ucar.tn",
                "Équipe projet ENICAR Connect (infra & données).",
                "Génie Informatique", "2ème année", "2ème Info Groupe C",
                skills("Docker", "CI/CD", "Spring Data JPA", "Git"));

        User mohamedDhiaAbidi = persistUser(encPwd, Role.STUDENT, "Mohamed Dhia Islem", "Abidi", "mohamed-dhia.abidi@enicar.ucar.tn",
                "Équipe projet ENICAR Connect (qualité / transversalité).",
                "Génie Informatique", "2ème année", "2ème Info Groupe C",
                skills("PostgreSQL", "Java", "Spring AOP"));

        User sarahMezzi = persistUser(encPwd, Role.STUDENT, "Sarah", "Mezzi", "sarah.mezzi@mecatronique.enicar.ucar.tn",
                "Étudiante en Mécatronique.", "Mécatronique", "3ème année", null,
                skills("PLC", "Capteurs", "Electronique analogique"));

        User hakimDrira = persistUser(encPwd, Role.STUDENT, "Hakim", "Drira", "hakim.drira@mecatronique.enicar.ucar.tn",
                "Club robotique.", "Mécatronique", "3ème année", null,
                skills("Arduino", "Mécatronique", "Asservissement"));

        User karimMansour = persistUser(encPwd, Role.STUDENT, "Karim", "Mansour", "karim.mansour@gm.enicar.ucar.tn",
                "Projets conception mécanique.", "Génie Mécanique", "3ème année", null,
                skills("Fabrication mécanique", "Dessin industriel"));

        User nourBenSalah = persistUser(encPwd, Role.STUDENT, "Nour El Houda", "Ben Salah", "nour.bensalah@gm.enicar.ucar.tn",
                "Laboratoires matériaux.", "Génie Mécanique", "3ème année", null,
                skills("Matériaux", "Essais mécaniques"));

        User alumniVermeg = persistUser(encPwd, Role.ALUMNI, "Amine", "Khelifi", "amine.khelifi@alumni.vermeg.tn",
                "Ingénieur logiciels chez Vermeg.", "Génie Informatique", null, null,
                skills("Java", "Spring Boot", "Angular"));

        User alumniSopra = persistUser(encPwd, Role.ALUMNI, "Layla", "Zarrouk", "layla.zarrouk@alumni.soprahr.tn",
                "Consultante RH & ATS chez Sopra HR.", "Génie Informatique", null, null,
                skills("Recrutement", "Talent acquisition"));

        AppGroup groupeGi = persistGroup(faouzi, "Génie Informatique",
                "Département Génie Informatique.", GroupType.FILIERE, GroupKind.CUSTOM);

        AppGroup groupeGm = persistGroup(faouzi, "Génie Mécanique",
                "Département Génie Mécanique.", GroupType.FILIERE, GroupKind.CUSTOM);

        AppGroup groupeMecaPlus = persistGroup(faouzi, "Mécatronique",
                "Département Mécatronique.", GroupType.FILIERE, GroupKind.CUSTOM);

        AppGroup groupeClasseC = persistGroup(mohamedJerbi, "2ème Info Groupe C",
                "Promotion 2ème année Info — Groupe de projet soutenance.", GroupType.PROMOTION, GroupKind.CUSTOM);

        AppGroup groupeCorps = persistGroup(faouzi, "Corps Enseignant",
                "Espace officiel pour enseignants et intervenants ENI Carthage.", GroupType.THEMATIC,
                GroupKind.CUSTOM);

        AppGroup groupeAlumni = persistGroup(faouzi, "Alumni ENICAR",
                "Communauté des diplômés ENICAR.", GroupType.CLUB, GroupKind.CUSTOM);

        addMembership(groupeGi, faouzi, MemberRole.ADMIN);
        addMembership(groupeGi, mohamedJerbi, MemberRole.MEMBER);
        addMembership(groupeGi, mohamedBabou, MemberRole.MEMBER);
        addMembership(groupeGi, mohamedDhiaAbidi, MemberRole.MEMBER);
        addMembership(groupeGi, alumniVermeg, MemberRole.MEMBER);
        addMembership(groupeGi, alumniSopra, MemberRole.MEMBER);

        addMembership(groupeGm, faouzi, MemberRole.ADMIN);
        addMembership(groupeGm, karimMansour, MemberRole.MEMBER);
        addMembership(groupeGm, nourBenSalah, MemberRole.MEMBER);

        addMembership(groupeMecaPlus, faouzi, MemberRole.ADMIN);
        addMembership(groupeMecaPlus, sarahMezzi, MemberRole.MEMBER);
        addMembership(groupeMecaPlus, hakimDrira, MemberRole.MEMBER);

        addMembership(groupeClasseC, mohamedBabou, MemberRole.MEMBER);
        addMembership(groupeClasseC, mohamedDhiaAbidi, MemberRole.MEMBER);

        addMembership(groupeCorps, faouzi, MemberRole.ADMIN);

        addMembership(groupeAlumni, faouzi, MemberRole.ADMIN);
        addMembership(groupeAlumni, alumniVermeg, MemberRole.MEMBER);
        addMembership(groupeAlumni, alumniSopra, MemberRole.MEMBER);

        String bodyFaouzi = "Bonjour à tous, les supports de cours Spring Boot et les TPs sont disponibles. "
                + "N'oubliez pas l'architecture IoC et l'utilisation de Lombok ! #SpringBoot #Lombok";

        postRepository.save(Post.builder()
                .author(faouzi)
                .visibility(Visibility.PUBLIC)
                .body(bodyFaouzi)
                .hashtags(extractHashtags(bodyFaouzi))
                .build());

        String bodyJerbi = "Ravi de vous présenter notre plateforme ENICAR Connect pour notre soutenance ! "
                + "#ENICARConnect #Projet";

        Post postJerbi = postRepository.save(Post.builder()
                .author(mohamedJerbi)
                .visibility(Visibility.PUBLIC)
                .body(bodyJerbi)
                .hashtags(extractHashtags(bodyJerbi))
                .group(groupeGi)
                .build());

        String bodyKarim = "Bonjour tout le monde — est-ce que vous avez une référence fiable pour le dimensionnement "
                + "rapide des guidages en CFAO ? Merci d'avance. #Questions #Meca";

        Post postKarimQuestion = postRepository.save(Post.builder()
                .author(karimMansour)
                .visibility(Visibility.PUBLIC)
                .group(groupeGm)
                .body(bodyKarim)
                .hashtags(extractHashtags(bodyKarim))
                .build());

        postLikeRepository.save(PostLike.builder().post(postJerbi).user(mohamedBabou).build());
        postLikeRepository.save(PostLike.builder().post(postJerbi).user(mohamedDhiaAbidi).build());

        commentRepository.save(Comment.builder()
                .post(postKarimQuestion)
                .author(mohamedDhiaAbidi)
                .text("Salut Karim — commence par les fiches LMS (rubrique CFAO guidages). "
                        + "Pour aller vite : check-list ISO sur jeux fonctionnels puis validation par simulation "
                        + "si ton prof l'exige.")
                .build());

        saveMessage(groupeClasseC, mohamedBabou,
                "On en est où pour le montage vidéo de la soutenance ?");
        saveMessage(groupeClasseC, mohamedJerbi,
                "Timeline figée dans l'outil montage — il reste voix-off + captions.");
        saveMessage(groupeClasseC, mohamedDhiaAbidi,
                "Je peux enregistrer la voix-off ce soir et vous envoyer le fichier audio.");
        saveMessage(groupeClasseC, mohamedBabou,
                "Parfait, j'intègre demain avant midi pour garder une marge de relecture.");

        JobOffer fullStack = JobOffer.builder()
                .title("Développeur Full-Stack")
                .company("Vermeg")
                .location("Tunis · Hybride")
                .type("Stage")
                .description(
                        "Rejoignez Vermeg pour un stage technique sur nos solutions bancaires. "
                                + "Vous contribuerez à des applications Spring Boot + Angular avec revue de code.")
                .tags("fullstack,springboot,angular,stage")
                .author(alumniVermeg)
                .requiredSkills(requiredSkillsSet("Spring Boot", "Angular"))
                .build();

        JobOffer devOps = JobOffer.builder()
                .title("Ingénieur DevOps")
                .company("Sopra HR")
                .location("Paris · Remote-friendly")
                .type("CDI")
                .description(
                        "Mise en place de pipelines CI/CD, automatisation Docker/Kubernetes et bonnes "
                                + "pratiques de livraison continue pour nos équipes produit.")
                .tags("docker,cicd,devops")
                .author(alumniSopra)
                .requiredSkills(requiredSkillsSet("Docker", "CI/CD"))
                .build();

        JobOffer mecaIndustrial = JobOffer.builder()
                .title("Ingénieur Méthodes & CFAO — assemblages mécano-soudés")
                .company("ManufactPro Tunisie")
                .location("Bizerte")
                .type("CDI")
                .description(
                        "Offre publiée par un partenaire recruteur Sopra HR : CFAO industrielle SOLIDWORKS, "
                                + "gammes fab et contrôle géométrique — profil très orienté conception mécanique.")
                .tags("cfa,solidworks,mecanique")
                .author(alumniSopra)
                .requiredSkills(requiredSkillsSet("SOLIDWORKS", "Mécanique des solides"))
                .build();

        jobOfferRepository.save(fullStack);
        jobOfferRepository.save(devOps);
        jobOfferRepository.save(mecaIndustrial);

        log.info("Comptes démo créés avec un mot de passe partagé BCrypt — à changer après soutenance (voir classe DatabaseSeeder).");
        log.info("=== INITIALISATION DES DONNÉES DE DÉMONSTRATION RÉUSSIE === utilisateurs={}, groupes={}, publications={}, offres={}",
                userRepository.count(),
                groupRepository.count(),
                postRepository.count(),
                jobOfferRepository.count());
    }

    private User persistUser(
            String encodedPassword,
            Role role,
            String firstName,
            String lastName,
            String email,
            String bio,
            String department,
            String level,
            String className,
            Set<String> skills) {

        User u = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(encodedPassword)
                .bio(bio)
                .department(department)
                .level(level)
                .className(className)
                .role(role)
                .skills(new LinkedHashSet<>(skills))
                .avatarColor(resolveRoleColor(role))
                .avatarBg(resolveRoleBg(role))
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
        return new LinkedHashSet<>(java.util.Arrays.asList(skills));
    }

    private Set<String> skills(String... raw) {
        return new LinkedHashSet<>(java.util.Arrays.asList(raw));
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
        if (name.contains("Mécanique")) {
            return "fas fa-gears";
        }
        if (name.contains("Mécatronique")) {
            return "fas fa-cogs";
        }
        if (name.contains("Enseignant")) {
            return "fas fa-chalkboard-teacher";
        }
        if (name.contains("Alumni")) {
            return "fas fa-user-graduate";
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
}
