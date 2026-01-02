package com.example.doctorhibernate.util;

import org.mindrot.jbcrypt.BCrypt;

public class SecurityUtil {
    public static String hashPassword(String plainText) {
        return BCrypt.hashpw(plainText, BCrypt.gensalt());
    }

    // Verifies the password (plain vs hash)
    public static boolean checkPassword(String plainText, String hashed) {
        return BCrypt.checkpw(plainText, hashed);
    }
}
