package tn.enicar.enicarconnect.model;

/**
 * Permissions granulaires associées aux rôles.
 */
public enum Permission {

    // Publications
    POST_CREATE,
    POST_DELETE_OWN,
    POST_DELETE_ANY,

    // Événements
    EVENT_VIEW,
    EVENT_CREATE,
    EVENT_MANAGE,

    // Ressources pédagogiques
    RESOURCE_VIEW,
    RESOURCE_UPLOAD,

    // Offres d'emploi / stages
    JOB_VIEW,
    JOB_POST,

    // Groupes
    GROUP_VIEW,
    GROUP_CREATE,

    // Gestion des utilisateurs
    USER_MANAGE,

    // Tableau de bord administratif
    DASHBOARD_VIEW,

    // Profil
    PROFILE_EDIT_OWN,

    // Messagerie
    MESSAGING_USE
}
