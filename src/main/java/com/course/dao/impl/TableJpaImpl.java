package com.course.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.course.dao.TableDao;
import com.course.entity.TableEntity;
import com.course.repository.TableRepository;

@Repository
public class TableJpaImpl implements TableDao {

    @Autowired
    private TableRepository tableRepository;

    @Override
    public void addTable(TableEntity tableEntity) {
        tableRepository.save(tableEntity);
    }

    @Override
    public TableEntity updateTable(TableEntity tableEntity) {
        return tableRepository.save(tableEntity);
    }

    @Override
    public TableEntity findById(Integer id) {
        return tableRepository.findById(id).orElse(null);
    }

    @Override
    public TableEntity findByCode(String code) {
        return tableRepository.findByCode(code);
    }

    @Override
    public TableEntity findByIdAndStatus(Integer id, String status) {
        return tableRepository.findByIdAndStatus(id, status);
    }

    @Override
    public List<TableEntity> findAll() {
        return tableRepository.findAll();
    }

}
