package com.course.dao;

import java.util.List;

import com.course.entity.OrderItemEntity;
import com.course.model.dto.OrderItemDto;

public interface OrderItemDao {

    void addAllOrderItem(List<OrderItemEntity> orderItemEntities);

    void updateOrderItem(List<OrderItemEntity> orderItemEntities);

    List<OrderItemEntity> findByOrderId(Long orderId);

    List<OrderItemEntity> getOrderItemPreview(Long id);

    List<OrderItemDto> getAllOrderItems(String code);

    List<OrderItemEntity> getOrderItemByOrderId(Long id);

}
