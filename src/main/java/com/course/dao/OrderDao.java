package com.course.dao;

import java.util.List;

import com.course.entity.OrderEntity;

public interface OrderDao {

    Long addOrder(OrderEntity orderEntity);

    void updateOrder(OrderEntity orderEntity);

    OrderEntity getOrderById(Long id);

    Boolean existsByMainOrderIsDelete(String mainOrderCode, Boolean isActive);

    List<OrderEntity> getOrderList(String mainOrderCode, Boolean isActive);

    List<OrderEntity> findAll();

    List<OrderEntity> getOrdersByMainOrderCode(String mainOrderCode);

    List<OrderEntity> getKitchenOrders(List<String> status);

    List<OrderEntity> getRunnerOrders(String status);
}
