package com.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.course.entity.MorderItemEntity;

@Repository
public interface MorderItemRepository extends JpaRepository<MorderItemEntity, Long> {

}
