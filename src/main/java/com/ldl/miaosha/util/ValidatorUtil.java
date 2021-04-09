package com.ldl.miaosha.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {

    private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");

    public static boolean isMobile(String moble) {
        if (moble == null) {
            return false;
        }
        Matcher matcher = mobile_pattern.matcher(moble);
        return matcher.matches();
    }
}
