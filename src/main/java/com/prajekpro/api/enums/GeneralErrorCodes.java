package com.prajekpro.api.enums;

import java.util.Arrays;
import java.util.Optional;

public enum GeneralErrorCodes {

	ERR_EMAIL_ID_EXISTS("601"),
	ERR_CONTACT_NO_EXISTS("602"),
	ERR_EMAIL_ID_REQUIRED("603"),
	ERR_CONTACT_NO_REQUIRED("604"),
	ERR_REQUIRED_FIELDS_NOT_SUPPLIED("605"),
	ERR_GENERIC_ERROR_MSSG("606"), 
	ERR_MAX_OTP_ATTEMPTS_EXEMPTED("607"), 
	ERR_INVALID_USER_ID("608"), 
	ERR_USER_NOT_REGISTERED("609"),
	ERR_RECORD_NOT_FOUND("610"),
	ERR_FILE_UPLOAD("611"),
	ERR_INVALID_PASSWORD_SUPPLIED("612"),
	ERR_DUPLICATE_RECORD_EXISTS("613"),
	ERR_INVALID_DATA_SUPPLIED("614"),
	ERR_INVALID_USERNAME("615"),
	ERR_ADMIN_DEACTIVATED("616"),
	ERR_CANCELLATION_TIME_EXCEEDS("617"),
	ERR_USER_ALREADY_REGISTERED("618"),
	ERR_INVALID_DATA_FOR_REGISTRATION("619"),
	ERR_INSUFFICIENT_BALANCE_IN_WALLET("620"),
	ERR_PAYMENT_CANCELLATION("621");

	private String value;
	
	GeneralErrorCodes(String value) {
		this.value = value;
	}
	
	public String value() {
		return this.value;
	}
	
	public static Optional<GeneralErrorCodes> getValueOf(String value) {
		
		return Arrays.stream(values())
                .filter(gec -> gec.value().equals(value))
                .findFirst();
	}
}
