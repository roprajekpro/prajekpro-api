package com.prajekpro.api.dto;

import lombok.*;

import java.io.*;

@Getter
@Setter
@NoArgsConstructor
public class AddPROSubscriptionDTO implements Serializable {
    private static final long serialVersionUID = 7912867101252883402L;
    private String appliedCouponCode;
}
