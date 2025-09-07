package com.course.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.course.entity.MorderEntity;

@Repository
public interface MorderRepository extends JpaRepository<MorderEntity, Long> {

	Boolean existsByTableNumberAndDate(Integer tableNumber,Date date);
}
