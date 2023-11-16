package com.project.clariti.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.project.clariti.service.FeeCalculatorService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class FeeCalculatorControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private FeeCalculatorService service;

	@InjectMocks
	private FeeCalculatorController controller;

	@Test
	public void testCalculateFee() throws Exception {

		// Performing the request and validating the response
		mockMvc.perform(MockMvcRequestBuilders.get("/calculateFee")
				.param("department", "IT")
				.param("category", "")
				.param("subCategory", "")
				.param("type", "")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
		.andExpect(MockMvcResultMatchers.content().string("There is no data for : IT"));

	}

}