package com.course.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.course.dao.OrderDao;
import com.course.entity.OrderEntity;
import com.course.repository.OrderRepository;

@Repository
public class OrderJpaImpl implements OrderDao {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Long addOrder(OrderEntity orderEntity) {
        OrderEntity saved = orderRepository.save(orderEntity);
        return saved.getId();
    }

    @Override
    public void updateOrder(OrderEntity orderEntity) {
        orderRepository.save(orderEntity);
    }

    @Override
    public OrderEntity getOrderById(Long id) {
        return orderRepository.getOrderById(id);
    }

    @Override
    public Boolean existsByMainOrderIsDelete(String mainOrderCode, Boolean isActive) {
        return orderRepository.existsByMainOrderCodeAndIsActive(mainOrderCode, isActive);
    }

    @Override
    public List<OrderEntity> getOrderList(String mainOrderCode, Boolean isActive) {
        return orderRepository.findByMainOrderCodeAndIsActive(mainOrderCode, isActive);
    }

    @Override
    public List<OrderEntity> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public List<OrderEntity> getOrdersByMainOrderCode(String mainOrderCode) {
        return orderRepository.findByMainOrderCode(mainOrderCode);
    }

    @Override
    public List<OrderEntity> getKitchenOrders(List<String> status) {
        return orderRepository.getKitchenOrders(status);
    }

    @Override
    public List<OrderEntity> getRunnerOrders(String status) {
        return orderRepository.findByOrderStatusOrderByCreatedAtAsc(status);
    }

}
