package com.course.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.course.entity.MenuEntity;

public interface MenuDao {

    void addMenu(MenuEntity menuEntity);

    void updateMenu(MenuEntity menuEntity);

    boolean existsByName(String name);

    MenuEntity findById(Long id);

    List<MenuEntity> findAllById(List<Long> menuList);

    List<MenuEntity> findAll();

    Page<MenuEntity> findAllActive(Pageable pageable);

    Page<MenuEntity> getMenuByCategory(Integer category, Pageable pageable);
}