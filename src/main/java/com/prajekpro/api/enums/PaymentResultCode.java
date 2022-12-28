package com.prajekpro.api.enums;

import java.util.Arrays;
import java.util.Optional;

public enum PaymentResultCode {
    AuthenticationFinished (1),AuthenticationNotRequired (2), Authorised(3),Cancelled(4),ChallengeShopper(5),
    Error(6),IdentifyShopper(7),Pending(8),PresentToShopper(9),Received(10),
    RedirectShopper(11),Refused(12);

    private Integer value;

    PaymentResultCode(Integer value) {
        this.value = value;
    }

    public static Optional<PaymentResultCode> valueOf(Integer value) {

        return Arrays
                .stream(values())
                .filter(
                        mt -> mt.value == value)
                .findFirst();
    }

    public Integer value() {
        return this.value;
    }
}
