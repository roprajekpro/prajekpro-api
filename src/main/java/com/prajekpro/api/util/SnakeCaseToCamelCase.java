package com.prajekpro.api.util;

import java.util.*;
import java.util.stream.*;

public class SnakeCaseToCamelCase {

    public static void main(String[] args) {
        String value = "ID\n" +
                "COUPON_CODE\n" +
                "VALIDITY_PER_USER\n" +
                "COUPON_CODE_TYPE\n" +
                "META_DATA\n" +
                "CREATED_BY\n" +
                "MODIFIED_BY\n" +
                "CREATED_TS\n" +
                "MODIFIED_TS\n" +
                "ACTIVE_STATUS";

        String delimiter = "\n";

        StringBuilder str1 = new StringBuilder(value.toLowerCase());
        List<Integer> indexesToMakeUpperCase = new ArrayList<>();

        for (int i = 0; i < str1.length(); i++) {
            if (str1.charAt(i) == '_') {
                str1.deleteCharAt(i);
                str1.replace(i, (i + 1), String.valueOf(Character.toUpperCase(str1.charAt(i))));
            }
        }

        Arrays.stream(str1.toString().split(delimiter)).map(a -> "private String " + a + ";").collect(Collectors.toList()).forEach(b -> System.out.println(b));
    }
}
