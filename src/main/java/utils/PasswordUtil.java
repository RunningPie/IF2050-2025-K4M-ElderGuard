package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    /**
     * Hashes a password with salt using SHA-256
     * @param password the plain text password
     * @return the hashed password with salt (format: salt:hash)
     */
    public static String hashPassword(String password) {
        try {
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Hash the password with salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            // Encode salt and hash to Base64
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedPassword);

            // Return format: salt:hash
            return saltBase64 + ":" + hashBase64;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Verifies a password against a stored hash
     * @param password the plain text password to verify
     * @param storedHash the stored hash (format: salt:hash)
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Split the stored hash into salt and hash parts
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }

            String saltBase64 = parts[0];
            String hashBase64 = parts[1];

            // Decode salt and hash from Base64
            byte[] salt = Base64.getDecoder().decode(saltBase64);
            byte[] storedHashBytes = Base64.getDecoder().decode(hashBase64);

            // Hash the provided password with the same salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            // Compare the hashes
            return MessageDigest.isEqual(storedHashBytes, hashedPassword);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error verifying password", e);
        } catch (Exception e) {
            // Invalid format or decoding error
            return false;
        }
    }

    /**
     * Generates a random password
     * @param length the length of the password
     * @return a random password
     */
    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    /**
     * Validates password strength
     * @param password the password to validate
     * @return true if password meets strength requirements, false otherwise
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }

        return hasLower && hasUpper && hasDigit && hasSpecial;
    }

    /**
     * Gets password strength description
     * @param password the password to check
     * @return description of password strength
     */
    public static String getPasswordStrengthDescription(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        }

        if (password.length() < 6) {
            return "Password too short (minimum 6 characters)";
        }

        if (password.length() < 8) {
            return "Weak - Use at least 8 characters";
        }

        if (isPasswordStrong(password)) {
            return "Strong";
        }

        return "Medium - Add uppercase, lowercase, numbers, and special characters";
    }
}