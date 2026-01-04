package presentation.ui.utils;

import java.util.regex.Pattern;

public class Validator {

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Phone validation pattern (Moroccan phone numbers)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+212|0)[6-7][0-9]{8}$");

    /**
     * Validates an email address
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates a phone number
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Validates that a string is not empty
     */
    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Validates name (no special characters, reasonable length)
     */
    public static boolean isValidName(String name) {
        if (!isNotEmpty(name)) {
            return false;
        }
        return name.trim().length() >= 2 && name.trim().length() <= 50 &&
                name.matches("^[A-Za-zÀ-ÿ\\s'-]+$");
    }

    /**
     * Validates password strength
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        // At least one digit, one letter, minimum 6 characters
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,}$");
    }

    /**
     * Gets validation error message for email
     */
    public static String getEmailError(String email) {
        if (!isNotEmpty(email)) {
            return "L'email est requis";
        }
        if (!isValidEmail(email)) {
            return "Format d'email invalide";
        }
        return null;
    }

    /**
     * Gets validation error message for phone
     */
    public static String getPhoneError(String phone) {
        if (!isNotEmpty(phone)) {
            return "Le téléphone est requis";
        }
        if (!isValidPhone(phone)) {
            return "Format de téléphone invalide (ex: 0612345678)";
        }
        return null;
    }

    /**
     * Gets validation error message for name
     */
    public static String getNameError(String name) {
        if (!isValidName(name)) {
            return "Nom invalide (2-50 caractères, lettres seulement)";
        }
        return null;
    }

    /**
     * Gets validation error message for password
     */
    public static String getPasswordError(String password) {
        if (!isValidPassword(password)) {
            return "Mot de passe faible (min 6 caractères, 1 lettre et 1 chiffre)";
        }
        return null;
    }
}
