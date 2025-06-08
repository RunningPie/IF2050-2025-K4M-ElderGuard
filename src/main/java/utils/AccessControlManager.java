package security;

import models.Role;
import models.Permission;
import models.UserAccount;
import utils.SessionManager;
import java.util.*;

public class AccessControlManager {

    private static final Map<Role, Set<Permission>> rolePermissions = new HashMap<>();

    static {
        // Initialize role permissions based on requirements
        initializeRolePermissions();
    }

    private static void initializeRolePermissions() {
        // FAMILY permissions: Dashboard, Emergency Alert, Account
        Set<Permission> familyPermissions = EnumSet.of(
                Permission.VIEW_DASHBOARD,
                Permission.VIEW_FAMILY_EMERGENCY_ALERTS,
                Permission.CREATE_EMERGENCY_ALERT,
                Permission.RESPOND_TO_EMERGENCY_ALERT,
                Permission.VIEW_OWN_ACCOUNT,
                Permission.EDIT_OWN_ACCOUNT,
                Permission.VIEW_FAMILY_ACCOUNTS,
                Permission.EDIT_FAMILY_ACCOUNTS
        );
        rolePermissions.put(Role.FAMILY, familyPermissions);

        // LANSIA permissions: Dashboard, Account
        Set<Permission> lansiaPermissions = EnumSet.of(
                Permission.VIEW_DASHBOARD,
                Permission.CREATE_EMERGENCY_ALERT,
                Permission.VIEW_OWN_ACCOUNT,
                Permission.EDIT_OWN_ACCOUNT
        );
        rolePermissions.put(Role.LANSIA, lansiaPermissions);

        // MEDICAL_STAFF permissions: Emergency Alert, Account
        Set<Permission> medicalPermissions = EnumSet.of(
                Permission.VIEW_ALL_EMERGENCY_ALERTS,
                Permission.RESPOND_TO_EMERGENCY_ALERT,
                Permission.VIEW_OWN_ACCOUNT,
                Permission.EDIT_OWN_ACCOUNT
        );
        rolePermissions.put(Role.MEDICAL_STAFF, medicalPermissions);

        // ADMIN permissions (all permissions)
        Set<Permission> adminPermissions = EnumSet.allOf(Permission.class);
        rolePermissions.put(Role.ADMIN, adminPermissions);
    }

    /**
     * Check if current user has specified permission
     */
    public static boolean hasPermission(Permission permission) {
        UserAccount currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        return hasPermission(currentUser.getUserRole(), permission);
    }

    /**
     * Check if specified role has permission
     */
    public static boolean hasPermission(Role role, Permission permission) {
        Set<Permission> permissions = rolePermissions.get(role);
        return permissions != null && permissions.contains(permission);
    }

    /**
     * Get all permissions for current user
     */
    public static Set<Permission> getCurrentUserPermissions() {
        UserAccount currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return Collections.emptySet();
        }
        return getRolePermissions(currentUser.getUserRole());
    }

    /**
     * Get all permissions for specified role
     */
    public static Set<Permission> getRolePermissions(Role role) {
        return rolePermissions.getOrDefault(role, Collections.emptySet());
    }

    /**
     * Check if current user can access emergency alerts
     */
    public static boolean canAccessEmergencyAlerts() {
        return hasPermission(Permission.VIEW_FAMILY_EMERGENCY_ALERTS) ||
                hasPermission(Permission.VIEW_ALL_EMERGENCY_ALERTS);
    }

    /**
     * Check if current user can view all emergency alerts (medical staff)
     */
    public static boolean canViewAllEmergencyAlerts() {
        return hasPermission(Permission.VIEW_ALL_EMERGENCY_ALERTS);
    }

    /**
     * Check if current user can access dashboard
     */
    public static boolean canAccessDashboard() {
        return hasPermission(Permission.VIEW_DASHBOARD);
    }

    /**
     * Check if current user can access account management
     */
    public static boolean canAccessAccountManagement() {
        return hasPermission(Permission.VIEW_OWN_ACCOUNT);
    }

    /**
     * Enforce permission check - throws exception if no permission
     */
    public static void requirePermission(Permission permission) throws SecurityException {
        if (!hasPermission(permission)) {
            UserAccount currentUser = SessionManager.getCurrentUser();
            String username = currentUser != null ? currentUser.getUsername() : "Unknown";
            throw new SecurityException("Access denied: User " + username +
                    " does not have permission " + permission);
        }
    }

    /**
     * Get available menu items for current user based on role
     */
    public static List<String> getAvailableMenuItems() {
        List<String> menuItems = new ArrayList<>();
        UserAccount currentUser = SessionManager.getCurrentUser();

        if (currentUser == null) {
            return menuItems;
        }

        Role role = currentUser.getUserRole();

        switch (role) {
            case FAMILY:
                menuItems.add("Dashboard");
                menuItems.add("Emergency Alerts");
                menuItems.add("Account");
                break;
            case LANSIA:
                menuItems.add("Dashboard");
                menuItems.add("Account");
                break;
            case MEDICAL_STAFF:
                menuItems.add("Emergency Alerts");
                menuItems.add("Account");
                break;
            case ADMIN:
                menuItems.add("Dashboard");
                menuItems.add("Emergency Alerts");
                menuItems.add("Account");
                menuItems.add("User Management");
                menuItems.add("System Settings");
                break;
        }

        return menuItems;
    }

    public static String getDefaultPageAfterLogin(Role role) {
        switch (role) {
            case FAMILY:
                return "/view/EmergencyAlertView.fxml";
            case LANSIA:
                return "/view/LansiaDashboardView.fxml";
            case MEDICAL_STAFF:
                return "/view/EmergencyAlertView.fxml";
            default:
                return "/view/LoginView.fxml";
        }
    }

    public static String getDashboardPath(Role role) {
        switch (role) {
            case FAMILY:
                return "/view/EmergencyAlertView.fxml";
            case LANSIA:
                return "/view/LansiaDashboardView.fxml";
            case MEDICAL_STAFF:
                return "/view/EmergencyAlertView.fxml";
            default:
                return "/view/LoginView.fxml";
        }
    }

    public static String getAccountPath(Role role) {
        switch (role) {
            case FAMILY:
                return "/view/AccountView.fxml";
            case LANSIA:
                return "/view/LansiaAccountView.fxml";
            case MEDICAL_STAFF:
                return "/view/AccountView.fxml";
            default:
                return "/view/LoginView.fxml";
        }
    }
}