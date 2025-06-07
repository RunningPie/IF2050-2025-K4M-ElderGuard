package model;

public enum Permission {
    // Dashboard permissions
    VIEW_DASHBOARD,

    // Emergency Alert permissions
    VIEW_FAMILY_EMERGENCY_ALERTS,
    VIEW_ALL_EMERGENCY_ALERTS,
    CREATE_EMERGENCY_ALERT,
    RESPOND_TO_EMERGENCY_ALERT,

    // Account permissions
    VIEW_OWN_ACCOUNT,
    EDIT_OWN_ACCOUNT,
    VIEW_FAMILY_ACCOUNTS,
    EDIT_FAMILY_ACCOUNTS,

    // Admin permissions
    VIEW_ALL_ACCOUNTS,
    EDIT_ALL_ACCOUNTS,
    DELETE_ACCOUNTS,
    MANAGE_SYSTEM_SETTINGS
}