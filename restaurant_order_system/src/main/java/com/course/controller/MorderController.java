package com.course.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.course.model.ApiResponse;
import com.course.model.MorderVo;
import com.course.service.MorderService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/order")
public class MorderController {

	@Autowired
	private MorderService morderService;

	@Operation(summary = "新增訂單資料", tags = "訂單")
	@PostMapping("/add")
	public ApiResponse<String> addMorder(@RequestBody MorderVo vo) {
		return morderService.addMorder(vo);
	}

	@Operation(summary = "更新訂單資料", tags = "訂單")
	@PostMapping("/update")
	public ApiResponse<String> updateMorder(@RequestBody MorderVo vo) {
		return morderService.updateMorder(vo);
	}

	@Operation(summary = "刪除訂單資料", tags = "訂單")
	@PostMapping("/delete/{code}")
	public ApiResponse<String> deleteMorder(@PathVariable String code) {
		return morderService.deleteMorder(code);
	}
}
