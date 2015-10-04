/**
 * 
 */
package com.foodroacher.app.android.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author visha
 *
 */
public final class Validator {
    private static final int PASSWORD_LENGTH = 8;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static boolean isvalidEmail(String email) {
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public static boolean isValidPassword(String password){
        return password!=null && !password.isEmpty() && password.length() >= PASSWORD_LENGTH ;
    }
}
