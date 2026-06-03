package com.course.dao;

import java.util.List;

import com.course.entity.TableEntity;

public interface TableDao {

    void addTable(TableEntity tableEntity);

    TableEntity updateTable(TableEntity tableEntity);

    TableEntity findById(Integer id);

    TableEntity findByCode(String code);

    TableEntity findByIdAndStatus(Integer id, String status);

    List<TableEntity> findAll();

}
