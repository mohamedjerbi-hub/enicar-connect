package tn.enicar.enicarconnect.config;

import org.springframework.stereotype.Component;
import tn.enicar.enicarconnect.model.Permission;
import tn.enicar.enicarconnect.model.Role;

import java.util.*;

import static tn.enicar.enicarconnect.model.Permission.*;
import static tn.enicar.enicarconnect.model.Role.*;

/**
 * Mapping rôle → ensemble de permissions.
 */
@Component
public class RolePermissionConfig {

    private static final Map<Role, Set<Permission>> ROLE_PERMISSIONS = new EnumMap<>(Role.class);

    static {
        // Permissions communes à tous
        Set<Permission> common = EnumSet.of(
                POST_CREATE, POST_DELETE_OWN,
                EVENT_VIEW, RESOURCE_VIEW, JOB_VIEW,
                GROUP_VIEW, GROUP_CREATE,
                PROFILE_EDIT_OWN, MESSAGING_USE);

        // STUDENT
        ROLE_PERMISSIONS.put(STUDENT, EnumSet.copyOf(common));

        // TEACHER = common + création évènements, upload ressources, poster offres
        Set<Permission> teacher = EnumSet.copyOf(common);
        teacher.addAll(EnumSet.of(EVENT_CREATE, RESOURCE_UPLOAD, JOB_POST));
        ROLE_PERMISSIONS.put(TEACHER, teacher);

        // ADMIN_STAFF = teacher + gestion évènements, gestion utilisateurs, dashboard
        Set<Permission> adminStaff = EnumSet.copyOf(teacher);
        adminStaff.addAll(EnumSet.of(EVENT_MANAGE, POST_DELETE_ANY, USER_MANAGE, DASHBOARD_VIEW));
        ROLE_PERMISSIONS.put(ADMIN_STAFF, adminStaff);

        // DIRECTION = toutes les permissions
        ROLE_PERMISSIONS.put(DIRECTION, EnumSet.allOf(Permission.class));

        // ALUMNI = common + poster offres
        Set<Permission> alumni = EnumSet.copyOf(common);
        alumni.add(JOB_POST);
        ROLE_PERMISSIONS.put(ALUMNI, alumni);
    }

    public Set<Permission> getPermissions(Role role) {
        return ROLE_PERMISSIONS.getOrDefault(role, EnumSet.noneOf(Permission.class));
    }

    public List<String> getPermissionNames(Role role) {
        return getPermissions(role).stream()
                .map(Enum::name)
                .sorted()
                .toList();
    }

    public boolean hasPermission(Role role, Permission permission) {
        return getPermissions(role).contains(permission);
    }
}
