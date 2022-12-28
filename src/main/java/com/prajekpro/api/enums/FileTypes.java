package com.prajekpro.api.enums;

import java.util.Arrays;
import java.util.Optional;

public enum FileTypes {
	DEFAULT(0),
	PDF(1);

	private Integer value;
	
	FileTypes(Integer value) {
		this.value = value;
	}
	
	public Integer value() {
		return this.value;
	}

	public static Optional<FileTypes> valueOf(Integer value) {

		return Arrays
				.stream(values())
				.filter(
						as -> as.value == value)
				.findFirst();
	}
}
