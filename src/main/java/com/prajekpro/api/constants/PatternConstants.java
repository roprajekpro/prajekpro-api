package com.prajekpro.api.constants;

import java.util.regex.*;

public class PatternConstants {

    public static final String PH_PHONE_NO_PATTERN = "^(\\+?09|\\+?639)\\d{9}$";

    public static final Pattern phPhoneNoPattern = Pattern.compile(PH_PHONE_NO_PATTERN);

    public static void main(String[] args) {
        System.out.println(phPhoneNoPattern.matcher("1639123456789").matches());
    }
}
