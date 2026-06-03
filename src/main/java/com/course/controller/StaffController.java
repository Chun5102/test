package com.course.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.course.model.request.StaffLoginRequest;
import com.course.model.request.StaffRequest;
import com.course.model.request.StaffUpdateRequest;
import com.course.model.response.ApiResponse;
import com.course.model.response.StaffResponse;
import com.course.service.StaffService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/staff")
public class StaffController {

	@Autowired
	private StaffService staffService;

	@Operation(summary = "員工登入", tags = "員工")
	@PostMapping("/login")
	public ApiResponse<String> staffLogin(HttpServletResponse response,
			@RequestBody StaffLoginRequest req) {
		return staffService.staffLogin(response, req.getUsername(), req.getPassword());
	}

	@Operation(summary = "員工登出", tags = "員工")
	@PostMapping("/logout")
	public ApiResponse<String> staffLogout(HttpServletResponse response) {
		return staffService.staffLogout(response);
	}

	@Operation(summary = "新增員工資料", tags = "員工")
	@PostMapping("/addStaff")
	public ApiResponse<String> addStaff(@Valid @RequestBody StaffRequest req) {
		return staffService.addStaff(req);
	}

	@Operation(summary = "更新員工資料", tags = "員工")
	@PutMapping("/updateStaff")
	public ApiResponse<String> updateStaff(@Valid @RequestBody StaffUpdateRequest req) {
		return staffService.updateStaff(req);
	}

	@Operation(summary = "變更員工狀態", tags = "員工")
	@PutMapping("/toggleStaffActive/{id}")
	public ApiResponse<String> toggleStaffActive(@PathVariable Long id) {
		return staffService.toggleStaffActive(id);
	}

	@Operation(summary = "取得員工資料(id)", tags = "員工")
	@GetMapping("/getStaffById/{id}")
	public ApiResponse<StaffResponse> getStaffById(@PathVariable Long id) {
		return staffService.getStaffById(id);
	}

	@Operation(summary = "搜索員工(name)", tags = "員工")
	@GetMapping("/select-like/{name}")
	public ApiResponse<List<StaffResponse>> findByName(@PathVariable String name) {
		return staffService.staffFindByName(name);
	}

	@Operation(summary = "取得所有員工", tags = "員工")
	@GetMapping("/getAllStaff")
	public ApiResponse<List<StaffResponse>> getAllStaff() {
		return staffService.getAllStaff();
	}
}
