package tn.enicar.enicarconnect.model;

/**
 * Catégories d'utilisateurs ENI Carthage.
 */
public enum Role {
    STUDENT, // Étudiants actuellement inscrits
    TEACHER, // Professeurs permanents, vacataires, intervenants externes
    ADMIN_STAFF, // Personnel administratif, technique, maintenance
    DIRECTION, // Directeur, directeurs des études, responsables de départements
    ALUMNI // Anciens diplômés
}
