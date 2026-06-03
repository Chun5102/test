package com.course.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.course.dao.OrderItemDao;
import com.course.entity.OrderItemEntity;
import com.course.model.dto.OrderItemDto;
import com.course.repository.OrderItemRepository;

@Repository
public class OrderItemJpaImpl implements OrderItemDao {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public void addAllOrderItem(List<OrderItemEntity> orderItemEntities) {
        orderItemRepository.saveAll(orderItemEntities);
    }

    @Override
    public void updateOrderItem(List<OrderItemEntity> orderItemEntities) {
        orderItemRepository.saveAll(orderItemEntities);
    }

    @Override
    public List<OrderItemEntity> findByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @Override
    public List<OrderItemEntity> getOrderItemPreview(Long id) {
        return orderItemRepository.findTop3ByOrderId(id);
    }

    @Override
    public List<OrderItemDto> getAllOrderItems(String code) {
        return orderItemRepository.getAllOrderItems(code);
    }

    @Override
    public List<OrderItemEntity> getOrderItemByOrderId(Long id) {
        return orderItemRepository.findAllByOrderId(id);
    }

}
