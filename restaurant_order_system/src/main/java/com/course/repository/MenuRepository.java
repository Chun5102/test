package com.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.course.entity.MenuEntity;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Long> {

	Boolean existsByName(String name);
}
