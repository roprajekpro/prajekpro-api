package com.prajekpro.api.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.prajekpro.api.PPApiApplication;
import com.prajekpro.api.constants.RestUrlConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(classes = PPApiApplication.class)
@AutoConfigureMockMvc
public class PPControllerTest {

	
	private final static String BASE_URL = "http://localhost:8080".concat(RestUrlConstants.PP_PUBLIC);
	private static final String PING_API = BASE_URL.concat(RestUrlConstants.PING);
	

	@Autowired private MockMvc mockMvc;
	
	@Test
	public void pingTest() {
		MockHttpServletResponse response;
		try {
			response = mockMvc
					.perform(
							MockMvcRequestBuilders.get(PING_API))
					.andReturn()
					.getResponse();
			log.info("response = " + response.getContentAsString());
			assertTrue(response.getStatus() == 200);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
