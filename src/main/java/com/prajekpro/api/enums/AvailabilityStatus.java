package com.prajekpro.api.enums;

import java.util.Arrays;
import java.util.Optional;

public enum AvailabilityStatus {

	UNAVAILABLE(0), 
	AVAILABLE(1);

	private Integer value;

	AvailabilityStatus(Integer value) {
		this.value = value;
	}

	public static Optional<AvailabilityStatus> valueOf(Integer value) {

		return Arrays
				.stream(values())
				.filter(
						as -> as.value == value)
				.findFirst();
	}
	public Integer value() {
		return this.value;
	}
}
