package model;

public enum Permission {
    // Dashboard permissions
    VIEW_DASHBOARD,

    // Emergency Alert permissions
    VIEW_FAMILY_EMERGENCY_ALERTS,     // FAMILY can see alerts for their family members only
    VIEW_ALL_EMERGENCY_ALERTS,       // MEDICAL_STAFF can see all emergency alerts
    CREATE_EMERGENCY_ALERT,
    RESPOND_TO_EMERGENCY_ALERT,

    // Account permissions
    VIEW_OWN_ACCOUNT,
    EDIT_OWN_ACCOUNT,
    VIEW_FAMILY_ACCOUNTS,             // FAMILY can view their family members' accounts
    VIEW_ALL_ACCOUNTS,                // ADMIN can view all accounts
    EDIT_FAMILY_ACCOUNTS,             // FAMILY can edit their family members' accounts
    EDIT_ALL_ACCOUNTS,                // ADMIN can edit all accounts
    DELETE_ACCOUNTS,                  // ADMIN only

    // System administration
    MANAGE_USERS,
    MANAGE_SYSTEM_SETTINGS,
    VIEW_SYSTEM_LOGS
}