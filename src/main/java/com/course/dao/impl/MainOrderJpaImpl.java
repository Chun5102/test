package com.course.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.course.dao.MainOrderDao;
import com.course.entity.MainOrderEntity;
import com.course.model.dto.TableStatusDto;
import com.course.repository.MainOrderRepository;

@Repository
public class MainOrderJpaImpl implements MainOrderDao {

    @Autowired
    private MainOrderRepository mainOrderRepository;

    @Override
    public void addMainOrder(MainOrderEntity mainOrderEntity) {
        mainOrderRepository.save(mainOrderEntity);
    }

    @Override
    public void updateMainOrder(MainOrderEntity mainOrderEntity) {
        mainOrderRepository.save(mainOrderEntity);
    }

    @Override
    public MainOrderEntity findByCode(String code) {
        return mainOrderRepository.findByCode(code);
    }

    @Override
    public MainOrderEntity getMainOrder(Integer tableId) {
        return mainOrderRepository.getMainOrder(tableId);
    }

    @Override
    public TableStatusDto getTableStatus(Integer id) {
        return mainOrderRepository.getTableStatus(id);
    }

    @Override
    public List<MainOrderEntity> getMainOrderByStatus(String paymentStatus, boolean isActive) {
        return mainOrderRepository.findByPaymentStatusAndIsActive(paymentStatus, isActive);
    }

    @Override
    public List<MainOrderEntity> getAllMainOrder() {
        return mainOrderRepository.findAll();
    }

}
