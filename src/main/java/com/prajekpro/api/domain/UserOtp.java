package com.prajekpro.api.domain;

import javax.persistence.*;

import com.safalyatech.common.domains.Auditable;

import com.safalyatech.common.domains.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "user_otp")
public class UserOtp extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	@Column(name = "OTP")
	private String otp;
	@Column(name = "CONTACT_NO")
	private String contactNo;
	@Column(name = "ATTEMPTS")
	private int attempts = 0;

	@OneToOne
	@JoinColumn(name = "FK_USER_ID")
	private Users user;

	UserOtp(String otp, String contactNo) {
		this.otp = otp;
		this.contactNo = contactNo;
	}
}
