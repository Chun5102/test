package com.course.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.course.entity.MenuEntity;
import com.course.entity.StaffEntity;
import com.course.model.request.MenuVo;
import com.course.model.request.StaffVo;

@Service
public class ServiceHelper {

	public StaffVo staffConvertToVo(StaffEntity staffEntity) 
	{
		StaffVo vo=new StaffVo();
		vo.setId(staffEntity.getId());
		vo.setName(staffEntity.getName());
		vo.setUsername(staffEntity.getUsername());
		vo.setPassword(staffEntity.getPassword());
		vo.setRole(staffEntity.getRole());
		
		return vo;
	}
	
	public StaffVo staffConvertToVoNoPassword(StaffEntity staffEntity) 
	{
		StaffVo vo=new StaffVo();
		vo.setId(staffEntity.getId());
		vo.setName(staffEntity.getName());
		vo.setUsername(staffEntity.getUsername());
		vo.setRole(staffEntity.getRole());
		
		return vo;
	}
	
	public MenuVo menuConvertToVo(MenuEntity menuEntity) 
	{
		MenuVo vo=new MenuVo();
		vo.setId(menuEntity.getId());
		vo.setName(menuEntity.getName());
		vo.setType(menuEntity.getType());
		vo.setPrice(menuEntity.getPrice());
		vo.setImg(menuEntity.getImg());
		vo.setDescription(menuEntity.getDescription());
		vo.setStatus(menuEntity.getStatus());
		
		return vo;
	}
}
