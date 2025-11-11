package com.course.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.course.model.request.MenuVo;
import com.course.model.response.ApiResponse;
import com.course.service.MenuService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/menu")
public class MenuController {

	@Autowired
	private MenuService menuService;

	@Operation(summary = "新增菜單資料", tags = "菜單")
	@PostMapping("/add")
	public ApiResponse addMenu(@Valid @ModelAttribute MenuVo vo) throws IOException {
		return menuService.addMenu(vo);
	}

	@Operation(summary = "更新菜單資料", tags = "菜單")
	@PostMapping("/update")
	public ApiResponse<String> updateMenu(@ModelAttribute MenuVo vo) throws IOException {
		return menuService.updateMenu(vo);
	}

	@Operation(summary = "刪除菜單資料", tags = "菜單")
	@PostMapping("/delete/{id}")
	public ApiResponse<String> deleteMenu(@PathVariable Long id) {
		return menuService.deleteMenu(id);
	}

	@Operation(summary = "搜索菜單(id)", tags = "菜單")
	@GetMapping("/select/{id}")
	public ApiResponse<MenuVo> findById(@PathVariable Long id) {
		return menuService.menuFindById(id);
	}

	@Operation(summary = "搜索菜單(type)", tags = "菜單")
	@GetMapping("/select-type/{type}")
	public ApiResponse<List<MenuVo>> findByType(@PathVariable Short type) {
		return menuService.menuFindByType(type);
	}

	@Operation(summary = "搜索菜單(name)", tags = "菜單")
	@GetMapping("/select-name/{name}")
	public ApiResponse<List<MenuVo>> findByName(@PathVariable String name) {
		return menuService.menuFindByName(name);
	}

}
