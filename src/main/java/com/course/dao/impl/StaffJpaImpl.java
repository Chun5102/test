package com.course.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.course.dao.StaffDao;
import com.course.entity.StaffEntity;
import com.course.repository.StaffRepository;

@Repository
public class StaffJpaImpl implements StaffDao {
    @Autowired
    private StaffRepository staffRepository;

    @Override
    public void addStaff(StaffEntity staffEntity) {
        staffRepository.save(staffEntity);
    }

    @Override
    public void updateStaff(StaffEntity staffEntity) {
        staffRepository.save(staffEntity);
    }

    @Override
    public StaffEntity findById(Long id) {
        return staffRepository.findById(id).orElse(null);
    }

    @Override
    public List<StaffEntity> findByNameLike(String name) {
        return staffRepository.findByNameLike(name);
    }

    @Override
    public StaffEntity getStaffDataByUsername(String username) {
        return staffRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return staffRepository.existsByUsername(username);
    }

    @Override
    public List<StaffEntity> findAll() {
        return staffRepository.findAll();
    }
}
