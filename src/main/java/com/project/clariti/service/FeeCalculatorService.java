package com.project.clariti.service;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.project.clariti.utils.Constants;

@Service
public class FeeCalculatorService{

	private static final Logger logger = LogManager.getLogger(FeeCalculatorService.class);

	@Value("${csv.file.path}")
	private String filePath;

	/**
	 * This method maps the id of csv file with the entire row data
	 * @param filePath
	 * @return csv data
	 * @throws RuntimeException
	 */
	public Map<String, List<String>> readCsvFile(String filePath) throws RuntimeException{
		try (CSVReader csvReader = new CSVReader(new FileReader(filePath))){
			List<String[]> records = csvReader.readAll();
				//mapping id with department list
				return records.stream()
						.skip(1) // skip header
						.collect(Collectors.toMap(columns -> columns[0], Arrays::asList));
		} catch (IOException | CsvException e) {
			logger.error("Error reading CSV file: {}", e.getMessage(), e);
			throw new RuntimeException("Error reading CSV file: " + e.getMessage(), e);
		}
	}

	/**
	 * This method calculates the base fee with surcharge of corresponding department
	 * @param department
	 * @param category
	 * @param subCategory
	 * @param type
	 * @return baseFeeWithSurCharge
	 * @throws RuntimeException
	 */
	public ResponseEntity<Object> getDepartmentFee(String department, String category, String subCategory,
			String type) throws RuntimeException{
		try {
			if(department.isBlank()) {
				return new ResponseEntity<>("Department is empty", HttpStatus.BAD_REQUEST);
			}else {
				// to calculate base fee
				Float baseFee = getBaseFee(department, category, subCategory, type);
				
				if(baseFee == 0) {
					
					String msg = "There is no data for : " +department+" "
							+category+" "+subCategory+" "+type;
					return new ResponseEntity<>(msg.trim(),HttpStatus.OK);
				}else if(Constants.DEPARTMENT_SURCHARGE_MAP.containsKey(department)) {
					
					// to calculate fee with surcharge
					float surcharge = Constants.DEPARTMENT_SURCHARGE_MAP.get(department);
					int baseFeeWithSurCharge = Math.round(baseFee + (baseFee * surcharge));
					return new ResponseEntity<>(baseFeeWithSurCharge,HttpStatus.OK);
				}else {
					
					return new ResponseEntity<>("No surcharge found for department: "+department+
							" only base fee:"+ Math.round(baseFee),HttpStatus.OK);
				}
			}
		} catch (Exception e) {
			// Log the exception for debugging purposes
			logger.error("Error while calculating the fee: {}", e.getMessage(), e);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	/**
	 * This method calculates the base fee based on department,category,subcategory 
	 * and type that matches with the csv data and multiplies Qty with price
	 * @return baseFee
	 */
	private Float getBaseFee(String department, String category, String subCategory, String type) {
		Float baseFee = 0f;
		Map<String, List<String>> feeDetails = readCsvFile(filePath);

		//to calculate the fee
		for(Entry<String, List<String>> feeDetail : feeDetails.entrySet()){
			if(isPresent(department, category, subCategory, type,feeDetail.getValue())) {
				try {
					baseFee = baseFee + Float.valueOf(feeDetail.getValue().get(Constants.QUANTITY_INDEX))
					* Float.valueOf(feeDetail.getValue().get(Constants.PRICE_INDEX));
				} catch (NumberFormatException ex) {
					// Handle invalid numeric values
					logger.error("Invalid numeric value in CSV: {}", ex.getMessage(), ex);
					throw new RuntimeException("Invalid numeric value in CSV: " + ex.getMessage(), ex);
				}
			}
		}
		return baseFee;
	}

	/**
	 * This method checks whether department,category,subcategory 
	 * or type matches with the csv data
	 * @return boolean
	 */
	private boolean isPresent(String department, String category, String subCategory,
			String type, List<String> feeDetail) {
		boolean isDepartment = department.isEmpty() || feeDetail.get(Constants.DEPARTMENT_INDEX).equals(department);
		boolean isCategory = category.isEmpty() || feeDetail.get(Constants.CATEGORY_INDEX).equals(category);
		boolean isSubCategory = subCategory.isEmpty() || feeDetail.get(Constants.SUBCATEGORY_INDEX).equals(subCategory);
		boolean isType = type.isEmpty() || feeDetail.get(Constants.TYPE_INDEX).equals(type);

		return isDepartment && isCategory && isSubCategory && isType;
	}
}