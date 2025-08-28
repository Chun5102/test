package com.course.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.course.model.StaffVo;
import com.course.service.StaffService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class StaffController {

	@Autowired
	private StaffService staffService;
	
	@Operation(summary = "員工登入",tags = "員工")
	@PostMapping("/staff-login")
	public ResponseEntity<StaffVo> staffLogin(@RequestParam String username,@RequestParam String password)
	{
		StaffVo vo=staffService.staffLogin(username, password);
		
		return ResponseEntity.ok().body(vo);
	}
}
