package com.course.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.course.dao.MainOrderDao;
import com.course.dao.MenuDao;
import com.course.dao.OrderDao;
import com.course.dao.OrderItemDao;
import com.course.dao.TableDao;
import com.course.entity.MainOrderEntity;
import com.course.entity.MenuEntity;
import com.course.entity.OrderEntity;
import com.course.entity.OrderItemEntity;
import com.course.entity.TableEntity;
import com.course.enums.ResultCode;
import com.course.model.request.OrderItemRequest;
import com.course.model.request.OrderRequest;
import com.course.model.request.OrderUpdateRequest;
import com.course.model.request.PayOrderRequest;
import com.course.model.response.ApiResponse;
import com.course.model.response.MainOrderAggregateResponse;
import com.course.model.response.MainOrderResponse;
import com.course.model.response.OrderItemResponse;
import com.course.model.response.OrderResponse;
import com.course.model.response.TableOrderResponse;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

	@Autowired
	private MainOrderDao mainOrderDao;

	@Autowired
	private OrderDao orderDao;

	@Autowired
	private OrderItemDao orderItemDao;

	@Autowired
	private TableDao tableDao;

	@Autowired
	private MenuDao menuDao;

	/*
	 * 新增訂單
	 */
	@Transactional
	public ApiResponse<Object> addOrder(OrderRequest req, Integer tableId) {
		// 檢查細項
		if (req.getOrderItem() == null || req.getOrderItem().isEmpty()) {
			return ApiResponse.error(ResultCode.ORDER_ITEM_IS_EMPTY);
		}

		// 取出菜單價格
		List<Long> menuIdList = req.getOrderItem().stream()
				.map(OrderItemRequest::getMenuId)
				.distinct() // 去除重複
				.toList();

		Map<Long, BigDecimal> priceMap = menuDao.findAllById(menuIdList).stream()
				.collect(Collectors.toMap(MenuEntity::getId, MenuEntity::getPrice));

		// 計算原始總價
		List<OrderItemEntity> orderItems = new ArrayList<>();
		BigDecimal calculatedTotalPrice = BigDecimal.ZERO;

		for (OrderItemRequest item : req.getOrderItem()) {
			BigDecimal price = priceMap.get(item.getMenuId());
			BigDecimal calculatedSubtotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));
			calculatedTotalPrice = calculatedTotalPrice.add(calculatedSubtotal);

			// 建立訂單細項
			OrderItemEntity orderItemEntity = OrderItemEntity.builder()
					.menuId(item.getMenuId())
					.menuName(item.getMenuName())
					.quantity(item.getQuantity())
					.subtotal(calculatedSubtotal)
					.isActive(true)
					.build();

			orderItems.add(orderItemEntity);
		}

		/*
		 * 取得使用中的桌子
		 * 防止主訂單同時新增
		 */
		TableEntity tableEntity = tableDao.findByIdAndStatus(tableId, "使用中");

		if (tableEntity != null && tableEntity.getOpenedAt() == null) {
			return ApiResponse.error(ResultCode.TABLE_NOT_OPEN);
		}

		// 取得未付款主訂單
		MainOrderEntity mainOrder = mainOrderDao.getMainOrder(tableId);

		// 建立 builder，必填欄位先設定
		OrderEntity.OrderEntityBuilder builder = OrderEntity.builder()
				.totalPrice(calculatedTotalPrice)
				.tableId(tableId)
				.orderStatus("待處理")
				.isActive(true);

		if (mainOrder != null) {
			builder.mainOrderCode(mainOrder.getCode());
			// 累加主訂單總價
			mainOrder.setTotalPrice(mainOrder.getTotalPrice().add(calculatedTotalPrice));
			mainOrderDao.updateMainOrder(mainOrder);
		} else {
			// 建立主訂單
			String mainOrderCode = UUID.randomUUID().toString();
			mainOrder = MainOrderEntity.builder()
					.code(mainOrderCode)
					.totalPrice(calculatedTotalPrice)
					.mainOrderStatus("已建立")
					.paymentStatus("未付款")
					.isActive(true)
					.tableId(tableId)
					.build();

			mainOrderDao.updateMainOrder(mainOrder);

			builder.mainOrderCode(mainOrderCode);

			// 更新桌子狀態
			tableEntity.setStatus("點餐");
			tableDao.updateTable(tableEntity);
		}

		// 建立訂單實體並存入資料庫
		OrderEntity orderEntity = builder.build();

		Long orderId = orderDao.addOrder(orderEntity);

		// 訂單細項加入Code後存入資料庫
		orderItems.forEach(item -> item.setOrderId(orderId));

		orderItemDao.addAllOrderItem(orderItems);

		// 回傳成功
		return ApiResponse.success();
	}

	/*
	 * 更新訂單
	 * 顧客無法使用
	 */
	@Transactional
	public ApiResponse<Object> updateOrder(OrderUpdateRequest req) {
		OrderEntity orderEntity = orderDao.getOrderById(req.getId());
		if (orderEntity == null) {
			return ApiResponse.error(ResultCode.ORDER_NOT_EXIST);
		}

		if (orderEntity.getOrderStatus().equals("已完成") || orderEntity.getOrderStatus().equals("已取消")) {
			return ApiResponse.error(ResultCode.ORDER_STATUS_INVALID);
		}
		orderEntity.setOrderStatus(req.getOrderStatus());

		orderDao.updateOrder(orderEntity);

		return ApiResponse.success("訂單更新成功");
	}

	@Transactional
	public ApiResponse<TableOrderResponse> deleteOrder(Long id, Integer tableId) {
		// 判斷訂單是否存在
		OrderEntity orderEntity = orderDao.getOrderById(id);
		if (orderEntity == null) {
			return ApiResponse.error(ResultCode.ORDER_NOT_EXIST);
		}
		// 判斷主訂單是否存在
		MainOrderEntity mainOrder = mainOrderDao.findByCode(orderEntity.getMainOrderCode());

		if (mainOrder == null) {
			return ApiResponse.error(ResultCode.MAIN_ORDER_NOT_EXIST);
		}

		// 判斷是否為該桌的訂單
		if (!mainOrder.getTableId().equals(tableId)) {
			return ApiResponse.error(ResultCode.NOT_OWN_TABLE_ORDER);
		}

		// 刪除訂單
		orderEntity.setIsActive(false);
		orderDao.updateOrder(orderEntity);

		List<OrderItemEntity> orderItemList = orderItemDao.findByOrderId(id);
		orderItemList.forEach(item -> item.setIsActive(false));

		orderItemDao.updateOrderItem(orderItemList);

		// 累加主訂單總價
		if (mainOrder.getTotalPrice() == null || orderEntity.getTotalPrice() == null) {
			return ApiResponse.error(ResultCode.ORDER_DATA_INVALID);
		}
		mainOrder.setTotalPrice(mainOrder.getTotalPrice().subtract(orderEntity.getTotalPrice()));

		// 判斷主訂單是否刪除
		Boolean hasActiveOrder = orderDao.existsByMainOrderIsDelete(mainOrder.getCode(), true);
		if (!hasActiveOrder) {
			mainOrder.setIsActive(false);

			TableEntity table = tableDao.findById(tableId);
			table.setStatus("使用中");
			tableDao.updateTable(table);
		}

		mainOrderDao.updateMainOrder(mainOrder);

		return getTableNotPayOrder(tableId);
	}

	@Transactional
	public ApiResponse<TableOrderResponse> cancelOrder(Long id) {
		OrderEntity orderEntity = orderDao.getOrderById(id);
		if (orderEntity == null) {
			return ApiResponse.error(ResultCode.ORDER_NOT_EXIST);
		}
		if (!"待處理".equals(orderEntity.getOrderStatus())) {
			System.out.println(orderEntity.getOrderStatus());
			return ApiResponse.error(ResultCode.ORDER_STATUS_INVALID);
		}
		orderEntity.setOrderStatus("已取消");

		orderDao.updateOrder(orderEntity);

		MainOrderEntity mainOrder = mainOrderDao.findByCode(orderEntity.getMainOrderCode());

		if (mainOrder.getTotalPrice() == null || orderEntity.getTotalPrice() == null) {
			return ApiResponse.error(ResultCode.ORDER_DATA_INVALID);
		}
		mainOrder.setTotalPrice(mainOrder.getTotalPrice().subtract(orderEntity.getTotalPrice()));

		mainOrderDao.updateMainOrder(mainOrder);

		return getTableNotPayOrder(mainOrder.getTableId());
	}

	public ApiResponse<Object> payOrder(String mainOrderCode, PayOrderRequest req) {
		MainOrderEntity mainOrder = mainOrderDao.findByCode(mainOrderCode);
		if (mainOrder == null) {
			return ApiResponse.error(ResultCode.MAIN_ORDER_NOT_EXIST);
		}

		mainOrder.setMainOrderStatus("已完成");
		mainOrder.setPaymentStatus("已付款");
		mainOrder.setPaymentMethod(req.getPaymentMethod());
		mainOrder.setPaidAmount(req.getPaidAmount());
		mainOrder.setChangeAmount(req.getChangeAmount());
		mainOrder.setPaidAt(LocalDateTime.now());

		mainOrderDao.updateMainOrder(mainOrder);

		TableEntity table = tableDao.findById(mainOrder.getTableId());
		table.setStatus("空閒");
		table.setOpenedAt(null);
		tableDao.updateTable(table);

		return ApiResponse.success();
	}

	public ApiResponse<TableOrderResponse> getTableNotPayOrder(Integer tableId) {
		MainOrderEntity mainOrder = mainOrderDao.getMainOrder(tableId);

		if (mainOrder == null) {
			return ApiResponse.error(ResultCode.MAIN_ORDER_NOT_EXIST);
		}

		// 建立桌號訂單總資料 builder
		TableOrderResponse.TableOrderResponseBuilder builder = TableOrderResponse.builder()
				.code(mainOrder.getCode())
				.tableId(mainOrder.getTableId())
				.totalPrice(mainOrder.getTotalPrice())
				.paymentStatus(mainOrder.getPaymentStatus());

		// 取得訂單
		List<OrderEntity> orderList = orderDao.getOrderList(mainOrder.getCode(), true);

		// 建立訂單和訂單細項回應
		List<OrderResponse> orderListRes = orderList.stream().map(order -> {
			List<OrderItemResponse> orderItems = orderItemDao.getOrderItemPreview(order.getId()).stream()
					.map(orderItem -> {
						OrderItemResponse orderItemRes = OrderItemResponse.builder()
								.menuName(orderItem.getMenuName())
								.quantity(orderItem.getQuantity())
								.build();
						return orderItemRes;
					}).collect(Collectors.toList());

			OrderResponse orderRes = OrderResponse.builder()
					.id(order.getId())
					.orderStatus(order.getOrderStatus())
					.totalPrice(order.getTotalPrice())
					.orderItems(orderItems)
					.build();
			return orderRes;
		}).collect(Collectors.toList());

		builder.orders(orderListRes);

		TableOrderResponse tableOrderRes = builder.build();

		return ApiResponse.success(tableOrderRes);
	}

	public ApiResponse<List<MainOrderResponse>> getUnpaidMainOrders() {

		List<MainOrderResponse> mainOrderList = mainOrderDao.getMainOrderByStatus("未付款", true).stream()
				.filter(mainOrder -> {
					return !"異常".equals(mainOrder.getMainOrderStatus());
				})
				.map(mainOrder -> {
					return mainOrderConvertToResponse(mainOrder);
				}).collect(Collectors.toList());

		return ApiResponse.success(mainOrderList);
	}

	public ApiResponse<List<MainOrderResponse>> getHistoryMainOrders() {

		List<MainOrderResponse> mainOrderList = mainOrderDao.getAllMainOrder().stream()
				.filter(mainOrder -> {
					return !mainOrder.getIsActive() ||
							!"未付款".equals(mainOrder.getPaymentStatus());
				})
				.map(mainOrder -> {
					return mainOrderConvertToResponse(mainOrder);
				}).collect(Collectors.toList());

		return ApiResponse.success(mainOrderList);
	}

	public ApiResponse<List<OrderResponse>> getOrdersByMainOrderCode(String mainOrderCode) {
		List<OrderResponse> orderList = orderDao.getOrdersByMainOrderCode(mainOrderCode).stream()
				.map(order -> {

					List<OrderItemResponse> orderItems = orderItemDao.getOrderItemByOrderId(order.getId()).stream()
							.map(orderItem -> {
								OrderItemResponse orderItemRes = OrderItemResponse.builder()
										.menuName(orderItem.getMenuName())
										.quantity(orderItem.getQuantity())
										.subtotal(orderItem.getSubtotal())
										.build();
								return orderItemRes;
							}).toList();

					OrderResponse orderRes = OrderResponse.builder()
							.id(order.getId())
							.createdAt(order.getCreatedAt())
							.orderStatus(order.getOrderStatus())
							.totalPrice(order.getTotalPrice())
							.orderItems(orderItems)
							.build();

					return orderRes;
				}).toList();

		return ApiResponse.success(orderList);
	}

	public ApiResponse<List<MainOrderAggregateResponse>> getUnpaidOrders() {

		List<MainOrderAggregateResponse> mainOrderList = mainOrderDao.getMainOrderByStatus("未付款", true).stream()
				.filter(mainOrder -> {
					return !"異常".equals(mainOrder.getMainOrderStatus());
				})
				.map(mainOrder -> {

					List<OrderResponse> orderList = orderDao.getOrdersByMainOrderCode(mainOrder.getCode()).stream()
							.map(order -> {

								List<OrderItemResponse> orderItems = orderItemDao.getOrderItemByOrderId(order.getId())
										.stream()
										.map(orderItem -> {
											OrderItemResponse orderItemRes = OrderItemResponse.builder()
													.menuName(orderItem.getMenuName())
													.quantity(orderItem.getQuantity())
													.subtotal(orderItem.getSubtotal())
													.build();
											return orderItemRes;
										}).toList();

								OrderResponse orderRes = OrderResponse.builder()
										.id(order.getId())
										.createdAt(order.getCreatedAt())
										.orderStatus(order.getOrderStatus())
										.totalPrice(order.getTotalPrice())
										.orderItems(orderItems)
										.build();

								return orderRes;
							}).toList();

					MainOrderAggregateResponse mainOrderRes = MainOrderAggregateResponse.builder()
							.code(mainOrder.getCode())
							.tableId(mainOrder.getTableId())
							.totalPrice(mainOrder.getTotalPrice())
							.mainOrderStatus(mainOrder.getMainOrderStatus())
							.batches(orderList)
							.build();
					return mainOrderRes;
				}).collect(Collectors.toList());
		return ApiResponse.success(mainOrderList);
	}

	public ApiResponse<List<OrderResponse>> getKitchenOrders() {
		List<OrderResponse> orderList = orderDao.getKitchenOrders(List.of("待處理", "準備中")).stream()
				.map(order -> {

					List<OrderItemResponse> orderItems = orderItemDao.getOrderItemByOrderId(order.getId()).stream()
							.map(orderItem -> {
								OrderItemResponse orderItemRes = OrderItemResponse.builder()
										.menuName(orderItem.getMenuName())
										.quantity(orderItem.getQuantity())
										.subtotal(orderItem.getSubtotal())
										.build();
								return orderItemRes;
							}).toList();

					MainOrderEntity mainOrder = mainOrderDao.findByCode(order.getMainOrderCode());
					OrderResponse orderRes = OrderResponse.builder()
							.id(order.getId())
							.tableId(mainOrder.getTableId())
							.createdAt(order.getCreatedAt())
							.orderStatus(order.getOrderStatus())
							.totalPrice(order.getTotalPrice())
							.orderItems(orderItems)
							.build();

					return orderRes;
				}).collect(Collectors.toList());

		return ApiResponse.success(orderList);
	}

	public ApiResponse<List<OrderResponse>> getRunnerOrders() {
		List<OrderResponse> orderList = orderDao.getRunnerOrders("待送餐").stream()
				.map(order -> {

					List<OrderItemResponse> orderItems = orderItemDao.getOrderItemByOrderId(order.getId()).stream()
							.map(orderItem -> {
								OrderItemResponse orderItemRes = OrderItemResponse.builder()
										.menuName(orderItem.getMenuName())
										.quantity(orderItem.getQuantity())
										.subtotal(orderItem.getSubtotal())
										.build();
								return orderItemRes;
							}).toList();

					MainOrderEntity mainOrder = mainOrderDao.findByCode(order.getMainOrderCode());
					OrderResponse orderRes = OrderResponse.builder()
							.id(order.getId())
							.tableId(mainOrder.getTableId())
							.createdAt(order.getCreatedAt())
							.orderStatus(order.getOrderStatus())
							.totalPrice(order.getTotalPrice())
							.orderItems(orderItems)
							.build();

					return orderRes;
				}).collect(Collectors.toList());

		return ApiResponse.success(orderList);
	}

	// 小方法
	private MainOrderResponse mainOrderConvertToResponse(MainOrderEntity mainOrder) {
		MainOrderResponse mainOrderRes = MainOrderResponse.builder()
				.code(mainOrder.getCode())
				.tableId(mainOrder.getTableId())
				.totalPrice(mainOrder.getTotalPrice())
				.mainOrderStatus(mainOrder.getMainOrderStatus())
				.paymentStatus(mainOrder.getPaymentStatus())
				.paymentMethod(mainOrder.getPaymentMethod())
				.paidAmount(mainOrder.getPaidAmount())
				.changeAmount(mainOrder.getChangeAmount())
				.paidAt(mainOrder.getPaidAt())
				.isActive(mainOrder.getIsActive())
				.build();
		return mainOrderRes;
	}

}
