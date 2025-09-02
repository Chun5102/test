package com.course.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.course.model.ApiResponse;
import com.course.model.MenuVo;
import com.course.service.MenuService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/menu")
public class MenuController {

	@Autowired
	private MenuService menuService;
	
	@Operation(summary = "新增菜單資料",tags = "菜單")
	@PostMapping("/add")
	public ApiResponse<String> addStaff(@Valid @ModelAttribute MenuVo vo,@RequestParam("file") MultipartFile image) throws IOException
	{		
		return menuService.addMenu(vo,image);
	}

}
