package com.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.course.entity.StaffEntity;

@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, Long>{

	StaffEntity findByUsernameAndPassword(String username,String password);
}
