package com.course.dao;

import java.util.List;

import com.course.entity.MainOrderEntity;
import com.course.model.dto.TableStatusDto;

public interface MainOrderDao {

    void addMainOrder(MainOrderEntity mainOrderEntity);

    void updateMainOrder(MainOrderEntity mainOrderEntity);

    MainOrderEntity findByCode(String code);

    MainOrderEntity getMainOrder(Integer tableId);

    TableStatusDto getTableStatus(Integer id);

    List<MainOrderEntity> getMainOrderByStatus(String paymentStatus, boolean isActive);

    List<MainOrderEntity> getAllMainOrder();

}
