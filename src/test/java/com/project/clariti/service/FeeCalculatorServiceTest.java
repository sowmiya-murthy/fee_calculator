package com.project.clariti.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.project.clariti.utils.Constants;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class FeeCalculatorServiceTest {

	@Autowired
	@InjectMocks
	private FeeCalculatorService feeCalculatorService;

	@Mock
	private CSVReader csvReader;

	@Value("${csv.file.path}")
	private String filePath;

	@Test
	void testReadCsvFile() throws Exception {
		// Call the method to test
		Map<String, List<String>> result = feeCalculatorService.readCsvFile(filePath);
		// Assertions
		assertEquals(14, result.size());
	}

	@Test
	public void testReadCsvFileException() {
		// Arrange
		FeeCalculatorService service = new FeeCalculatorService();

		// Act and Assert
		assertThrows(RuntimeException.class, () -> service.readCsvFile("nonexistentfile.csv"));
	}

	@Test
	public void testGetDepartmentFee() throws IOException, CsvException {
		// Test getDepartmentFee method
		ResponseEntity<Object> result = feeCalculatorService.getDepartmentFee(Constants.MARKETING_DEPARTMENT, "", "", "");
		assertTrue(result.getStatusCode().is2xxSuccessful());
		assertEquals(1235, result.getBody());
	}

	@Test
	public void testGetDepartmentFeeWithCat() throws IOException, CsvException {
		// Test getDepartmentFee method
		ResponseEntity<Object> result = feeCalculatorService.getDepartmentFee(Constants.OPERATIONS_DEPARTMENT, "Human Resources", "", "");
		assertTrue(result.getStatusCode().is2xxSuccessful());
		assertEquals(354, result.getBody());

		ResponseEntity<Object> resultWithNoCatMatch = feeCalculatorService.getDepartmentFee(Constants.OPERATIONS_DEPARTMENT, "Human", "", "");
		assertTrue(resultWithNoCatMatch.getStatusCode().is2xxSuccessful());
		assertEquals("There is no data for : Operations Human", resultWithNoCatMatch.getBody());
	}

	@Test
	public void testGetDepartmentFeeWithSub() throws IOException, CsvException {
		// Test getDepartmentFee method
		ResponseEntity<Object> result = feeCalculatorService.getDepartmentFee(Constants.OPERATIONS_DEPARTMENT, "Human Resources", "Cat1", "");
		assertTrue(result.getStatusCode().is2xxSuccessful());
		assertEquals(263, result.getBody());

		ResponseEntity<Object> resultWithNoSubMatch = feeCalculatorService.getDepartmentFee(Constants.OPERATIONS_DEPARTMENT, "Human Resources", "Cat", "");
		assertTrue(resultWithNoSubMatch.getStatusCode().is2xxSuccessful());
		assertEquals("There is no data for : Operations Human Resources Cat", resultWithNoSubMatch.getBody());
	}

	@Test
	public void testGetDepartmentFeeWithType() throws IOException, CsvException {
		// Test getDepartmentFee method
		ResponseEntity<Object> result = feeCalculatorService.getDepartmentFee(Constants.OPERATIONS_DEPARTMENT, "Human Resources", "Cat1", "TypeC");
		assertTrue(result.getStatusCode().is2xxSuccessful());
		assertEquals(263, result.getBody());

		ResponseEntity<Object> resultWithNoSubMatch = feeCalculatorService.getDepartmentFee(Constants.OPERATIONS_DEPARTMENT, "Human Resources", "Cat1", "Type");
		assertTrue(resultWithNoSubMatch.getStatusCode().is2xxSuccessful());
		assertEquals("There is no data for : Operations Human Resources Cat1 Type", resultWithNoSubMatch.getBody());
	}

	@Test
	public void testGetDepartmentFeeWithNoDepartment() throws IOException, CsvException {
		// Test getDepartmentFee method
		ResponseEntity<Object> result = feeCalculatorService.getDepartmentFee("", "", "", "");
		assertTrue(result.getStatusCode().is4xxClientError());
		assertEquals("Department is empty", result.getBody());
	}

	@Test
	public void testGetDepartmentFeeWithDepartmentNoSur() throws IOException, CsvException {
		// Test getDepartmentFee method
		ResponseEntity<Object> result = feeCalculatorService.getDepartmentFee("Operations78", "", "", "");
		assertTrue(result.getStatusCode().is2xxSuccessful());
		assertEquals("No surcharge found for department: Operations78 only base fee:200", result.getBody());
	}

	@Test
	public void testGetDepartmentFeeWithNumberFormatException() throws IOException, CsvException {

		ResponseEntity<Object> result = feeCalculatorService.getDepartmentFee(Constants.SALES_DEPARTMENT, "", "", "");

		assertTrue(result.getStatusCode().is5xxServerError());
		assertEquals("Invalid numeric value in CSV: For input string: \"jkk\"", result.getBody());
	}


}