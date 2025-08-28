package com.course.service;

import org.springframework.stereotype.Service;

import com.course.entity.StaffEntity;
import com.course.model.StaffVo;

@Service
public class StaffServiceHelper {

	public StaffVo convertToVo(StaffEntity staffEntity) 
	{
		StaffVo vo=new StaffVo();
		vo.setName(staffEntity.getName());
		vo.setUsername(staffEntity.getUsername());
		vo.setRole(staffEntity.getRole());
		
		return vo;
	}
}
