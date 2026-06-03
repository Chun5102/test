package com.course.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.course.annotation.RequireTableToken;
import com.course.model.request.OrderRequest;
import com.course.model.request.OrderUpdateRequest;
import com.course.model.request.PayOrderRequest;
import com.course.model.response.ApiResponse;
import com.course.model.response.MainOrderAggregateResponse;
import com.course.model.response.MainOrderResponse;
import com.course.model.response.OrderResponse;
import com.course.model.response.TableOrderResponse;
import com.course.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/order")
@Tag(name = "訂單", description = "訂單相關 API")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@Operation(summary = "新增訂單資料", tags = "訂單")
	@RequireTableToken
	@PostMapping("/addOrder")
	public ApiResponse<Object> addOrder(@Valid @RequestBody OrderRequest req, HttpServletRequest request) {
		Integer tableId = (Integer) request.getAttribute("tableId");
		return orderService.addOrder(req, tableId);
	}

	@Operation(summary = "更新訂單資料", tags = "訂單")
	@PutMapping("/updateOrder")
	public ApiResponse<Object> updateOrder(@Valid @RequestBody OrderUpdateRequest req) {
		return orderService.updateOrder(req);
	}

	@Operation(summary = "刪除訂單資料", tags = "訂單")
	@RequireTableToken
	@PutMapping("/deleteOrder/{id}")
	public ApiResponse<TableOrderResponse> deleteOrder(@PathVariable Long id, HttpServletRequest request) {
		Integer tableId = (Integer) request.getAttribute("tableId");
		return orderService.deleteOrder(id, tableId);
	}

	@Operation(summary = "取消訂單資料(顧客)", tags = "訂單")
	@RequireTableToken
	@PutMapping("/cancelOrderByCustomer/{id}")
	public ApiResponse<TableOrderResponse> cancelOrderByCustomer(@PathVariable Long id) {
		return orderService.cancelOrder(id);
	}

	@Operation(summary = "取消訂單資料(員工)", tags = "訂單")
	@PutMapping("/cancelOrderByStaff/{id}")
	public ApiResponse<TableOrderResponse> cancelOrderByStaff(@PathVariable Long id) {
		return orderService.cancelOrder(id);
	}

	@Operation(summary = "訂單結帳", tags = "訂單")
	@PutMapping("/payOrder")
	public ApiResponse<Object> payOrder(@RequestParam(value = "code") String mainOrderCode,
			@RequestBody PayOrderRequest req) {
		return orderService.payOrder(mainOrderCode, req);
	}

	@Operation(summary = "取得未付款訂單", tags = "訂單")
	@GetMapping("/getTableOrder")
	public ApiResponse<TableOrderResponse> getTableNotPayOrder(HttpServletRequest request) {
		Integer tableId = (Integer) request.getAttribute("tableId");
		return orderService.getTableNotPayOrder(tableId);
	}

	@Operation(summary = "取得未付款主訂單", tags = "訂單")
	@GetMapping("/getUnpaidMainOrders")
	public ApiResponse<List<MainOrderResponse>> getUnpaidMainOrders() {
		return orderService.getUnpaidMainOrders();
	}

	@Operation(summary = "取得歷史主訂單", tags = "訂單")
	@GetMapping("/getHistoryMainOrders")
	public ApiResponse<List<MainOrderResponse>> getHistoryMainOrders() {
		return orderService.getHistoryMainOrders();
	}

	@Operation(summary = "取得主訂單的所有訂單", tags = "訂單")
	@GetMapping("/getOrdersByMainOrderCode")
	public ApiResponse<List<OrderResponse>> getOrdersByMainOrderCode(
			@RequestParam(value = "code") String mainOrderCode) {
		return orderService.getOrdersByMainOrderCode(mainOrderCode);
	}

	@Operation(summary = "取得未付款訂單列表", tags = "訂單")
	@GetMapping("/getUnpaidOrders")
	public ApiResponse<List<MainOrderAggregateResponse>> getUnpaidOrders() {
		return orderService.getUnpaidOrders();
	}

	@Operation(summary = "取得廚房訂單", tags = "訂單")
	@GetMapping("/getKitchenOrders")
	public ApiResponse<List<OrderResponse>> getKitchenOrders() {
		return orderService.getKitchenOrders();
	}

	@Operation(summary = "取得可送餐訂單", tags = "訂單")
	@GetMapping("getRunnerOrders")
	public ApiResponse<List<OrderResponse>> getRunnerOrders() {
		return orderService.getRunnerOrders();
	}
}
