package com.course.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.course.dao.MenuDao;
import com.course.entity.MenuEntity;
import com.course.repository.MenuRepository;

@Repository
public class MenuJpaImpl implements MenuDao {

    @Autowired
    private MenuRepository menuRepository;

    @Override
    public void addMenu(MenuEntity menuEntity) {
        menuRepository.save(menuEntity);
    }

    @Override
    public void updateMenu(MenuEntity menuEntity) {
        menuRepository.save(menuEntity);
    }

    @Override
    public boolean existsByName(String name) {
        return menuRepository.existsByName(name);
    }

    @Override
    public MenuEntity findById(Long id) {
        return menuRepository.findById(id).orElse(null);
    }

    @Override
    public List<MenuEntity> findAll() {
        return menuRepository.findAll();
    }

    @Override
    public Page<MenuEntity> getMenuByCategory(Integer category, Pageable pageable) {
        return menuRepository.getMenuByCategory(category, pageable);
    }

    @Override
    public Page<MenuEntity> findAllActive(Pageable pageable) {
        return menuRepository.findAllActive(pageable);
    }

    @Override
    public List<MenuEntity> findAllById(List<Long> menuList) {
        return menuRepository.findAllById(menuList);
    }
}
