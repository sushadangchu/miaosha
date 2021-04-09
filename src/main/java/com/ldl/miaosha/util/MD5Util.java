package com.ldl.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    public static String MD5(String src) {
        return DigestUtils.md5Hex(src);
    }

    public static final String salt = "1a2b3c4d";

    public static String inputPassToFromPass(String inputPass) {
        String src = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return MD5(src);
    }

    public static String fromPassToDbPass(String fromPass, String salt) {
        String src = "" + salt.charAt(0) + salt.charAt(2) + fromPass + salt.charAt(5) + salt.charAt(4);
        return MD5(src);
    }

    public static String inputPassToDbPass(String inputPass, String saltDb) {
        String fromPass = inputPassToFromPass(inputPass);
        return fromPassToDbPass(fromPass, saltDb);
    }

    public static void main(String[] args) {
        System.out.println(inputPassToDbPass("123456", "1a2b3c4d"));
    }
}
