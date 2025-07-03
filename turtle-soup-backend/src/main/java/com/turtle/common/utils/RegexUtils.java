package com.turtle.common.utils;

public class RegexUtils {
    public static boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-z]{2,}$";
        return email != null && email.matches(regex);
    }
}
