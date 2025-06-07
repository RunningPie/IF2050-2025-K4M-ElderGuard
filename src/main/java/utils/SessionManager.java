package utils;

import model.UserAccount;
import model.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public class SessionManager {

    private static UserAccount currentUser;
    private static LocalDateTime loginTime;
    private static final int SESSION_TIMEOUT_MINUTES = 60; // 1 hour

    /**
     * Sets the current logged-in user
     * @param user the user account
     */
    public static void setCurrentUser(UserAccount user) {
        currentUser = user;
        loginTime = LocalDateTime.now();

        if (user != null) {
            System.out.println("User session started: " + user.getUsername() + " (" + user.getUserRole() + ")");
        }
    }

    /**
     * Gets the current logged-in user
     * @return the current user account, or null if not logged in
     */
    public static UserAccount getCurrentUser() {
        if (currentUser != null && isSessionValid()) {
            return currentUser;
        }

        // Session expired or no user
        if (currentUser != null) {
            System.out.println("Session expired for user: " + currentUser.getUsername());
            clearSession();
        }

        return null;
    }

    /**
     * Gets the current user's ID
     * @return the user ID, or null if not logged in
     */
    public static UUID getCurrentUserId() {
        UserAccount user = getCurrentUser();
        return user != null ? user.getUserID() : null;
    }

    /**
     * Gets the current user's role
     * @return the user role, or null if not logged in
     */
    public static Role getCurrentUserRole() {
        UserAccount user = getCurrentUser();
        return user != null ? user.getUserRole() : null;
    }

    /**
     * Gets the current user's username
     * @return the username, or null if not logged in
     */
    public static String getCurrentUsername() {
        UserAccount user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    /**
     * Checks if a user is currently logged in
     * @return true if logged in and session is valid, false otherwise
     */
    public static boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    /**
     * Checks if the current session is valid (not expired)
     * @return true if session is valid, false otherwise
     */
    public static boolean isSessionValid() {
        if (loginTime == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = loginTime.plusMinutes(SESSION_TIMEOUT_MINUTES);

        return now.isBefore(expiryTime);
    }

    /**
     * Refreshes the session timeout
     */
    public static void refreshSession() {
        if (currentUser != null) {
            loginTime = LocalDateTime.now();
        }
    }

    /**
     * Checks if the current user has a specific role
     * @param role the role to check
     * @return true if user has the role, false otherwise
     */
    public static boolean hasRole(Role role) {
        Role currentRole = getCurrentUserRole();
        return currentRole != null && currentRole == role;
    }

    /**
     * Checks if the current user is an admin
     * @return true if user is admin, false otherwise
     */
    public static boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

    /**
     * Checks if the current user is a lansia
     * @return true if user is lansia, false otherwise
     */
    public static boolean isLansia() {
        return hasRole(Role.LANSIA);
    }

    /**
     * Checks if the current user is a family member
     * @return true if user is family member, false otherwise
     */
    public static boolean isFamily() {
        return hasRole(Role.FAMILY);
    }

    /**
     * Checks if the current user is medical staff
     * @return true if user is medical staff, false otherwise
     */
    public static boolean isMedicalStaff() {
        return hasRole(Role.MEDICAL_STAFF);
    }

    /**
     * Clears the current session (logout)
     */
    public static void clearSession() {
        if (currentUser != null) {
            System.out.println("User session ended: " + currentUser.getUsername());
        }

        currentUser = null;
        loginTime = null;
    }

    /**
     * Gets the time when the user logged in
     * @return the login time, or null if not logged in
     */
    public static LocalDateTime getLoginTime() {
        return loginTime;
    }

    /**
     * Gets the time when the session will expire
     * @return the expiry time, or null if not logged in
     */
    public static LocalDateTime getSessionExpiryTime() {
        if (loginTime != null) {
            return loginTime.plusMinutes(SESSION_TIMEOUT_MINUTES);
        }
        return null;
    }

    /**
     * Gets the remaining session time in minutes
     * @return remaining minutes, or 0 if session expired/not logged in
     */
    public static long getRemainingSessionMinutes() {
        if (loginTime == null) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = loginTime.plusMinutes(SESSION_TIMEOUT_MINUTES);

        if (now.isAfter(expiryTime)) {
            return 0;
        }

        return java.time.Duration.between(now, expiryTime).toMinutes();
    }

    /**
     * Logs an action for the current user
     * @param action the action description
     */
    public static void logUserAction(String action) {
        if (currentUser != null) {
            System.out.println("[" + LocalDateTime.now() + "] " +
                    currentUser.getUsername() + " (" + currentUser.getUserRole() + "): " + action);
        }
    }
}