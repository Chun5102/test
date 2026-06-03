package com.course.dao;

import java.util.List;

import com.course.entity.StaffEntity;

public interface StaffDao {

    void addStaff(StaffEntity staffEntity);

    void updateStaff(StaffEntity staffEntity);

    StaffEntity findById(Long id);

    List<StaffEntity> findByNameLike(String name);

    StaffEntity getStaffDataByUsername(String username);

    boolean existsByUsername(String username);

    List<StaffEntity> findAll();
}
