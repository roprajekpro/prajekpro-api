package com.prajekpro.api.util;

import com.prajekpro.api.enums.*;
import com.safalyatech.common.enums.*;

import java.util.*;

public class CommonUtil {


    public static Set<String> getRoles(String source) {
        Set<String> roleMasterDtlSet = new HashSet<>();
        System.out.println("source = " + source);
        if (source.equalsIgnoreCase(Source.CUSTOMER_APP.name().toLowerCase(Locale.ROOT)))
            roleMasterDtlSet.add(Roles.ROLE_CUSTOMER.name());
        else if (source.equalsIgnoreCase(Source.PRO_APP.name().toLowerCase(Locale.ROOT)))
            roleMasterDtlSet.add(Roles.ROLE_VENDOR.name());
        else if (source.equalsIgnoreCase(Source.WEB.name().toLowerCase(Locale.ROOT))) {
            roleMasterDtlSet.add(Roles.ROLE_ADMINISTRATOR.name());
            roleMasterDtlSet.add(Roles.ROLE_ADMIN.name());
        } else
            roleMasterDtlSet.add(Roles.ROLE_DEFAULT.name());

        return roleMasterDtlSet;
    }

    public static boolean isSuccessfulPayment(PPPaymentResponseType paymentResponse) {
        return paymentResponse == PPPaymentResponseType.SUCCESS;
    }

    public static String getLikeClauseTerm(String term) {
        return "%" + term.trim().toLowerCase() + "%";
    }

    public static String convertTotalJobsCompleted(int totalJobsCompleted, int multiple) {

        if (totalJobsCompleted > 0) {
            StringBuilder sb = new StringBuilder();
            if (totalJobsCompleted > multiple) {
                int modRemainder = totalJobsCompleted % multiple;
                int totalJobsToDisplayInt = totalJobsCompleted - modRemainder;
                sb.append(totalJobsToDisplayInt).append("+");
                return sb.toString();
            } else {
                return sb.append(totalJobsCompleted).toString();
            }
        } else {
            return "0";
        }
    }
}
