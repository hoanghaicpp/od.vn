package com.example.od_vn;

import java.util.regex.*;

public class UserValidator {
    private Pattern usernamePattern;
    private Pattern passwordPattern;
    private Pattern emailPattern;

    private Matcher matcher;

    private static final String USERNAME_PATTERN =
            "^[a-zA-Z0-9._-]{3,20}$";
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public UserValidator() {
        usernamePattern = Pattern.compile(USERNAME_PATTERN);
        passwordPattern = Pattern.compile(PASSWORD_PATTERN);
        emailPattern = Pattern.compile(EMAIL_PATTERN);
    }

    public boolean validateUsername(final String username) {
        matcher = usernamePattern.matcher(username);
        return matcher.matches();
    }

    public boolean validatePassword(final String password) {
        matcher = passwordPattern.matcher(password);
        return matcher.matches();
    }

    public boolean validateEmail(final String email) {
        matcher = emailPattern.matcher(email);
        return matcher.matches();
    }
}