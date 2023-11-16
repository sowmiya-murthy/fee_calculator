package com.project.clariti.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.clariti.service.FeeCalculatorService;

@RestController
@RequestMapping("/")
public class FeeCalculatorController {

	@Autowired
	public FeeCalculatorService service;

	@GetMapping(value = "calculateFee")
	public ResponseEntity<Object> getDepartmentFee(
			@RequestParam(name="department") String department,
			@RequestParam(name="category",required = false, defaultValue = "") String category,
			@RequestParam(name="subCategory",required = false, defaultValue = "") String subCategory,
			@RequestParam(name="type",required = false, defaultValue = "") String type){
		return service.getDepartmentFee(department,category,subCategory,type);
	}

}
