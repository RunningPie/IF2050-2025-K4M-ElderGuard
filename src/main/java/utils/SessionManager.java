package utils;

import models.UserAccount;

public class SessionManager {
    private static UserAccount currentUser;
    private static long sessionStartTime;
    private static final long SESSION_TIMEOUT = 30 * 60 * 1000; // 30 minutes

    public static void setCurrentUser(UserAccount user) {
        currentUser = user;
        sessionStartTime = System.currentTimeMillis();
    }

    public static UserAccount getCurrentUser() {
        if (currentUser != null && isSessionValid()) {
            return currentUser;
        }
        return null;
    }

    public static boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    public static boolean isSessionValid() {
        if (currentUser == null) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        return (currentTime - sessionStartTime) < SESSION_TIMEOUT;
    }

    public static void refreshSession() {
        if (currentUser != null) {
            sessionStartTime = System.currentTimeMillis();
        }
    }

    public static void clearSession() {
        currentUser = null;
        sessionStartTime = 0;
    }

    public static long getSessionRemainingTime() {
        if (currentUser == null) {
            return 0;
        }

        long elapsed = System.currentTimeMillis() - sessionStartTime;
        long remaining = SESSION_TIMEOUT - elapsed;
        return Math.max(0, remaining);
    }
}