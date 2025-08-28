package com.course.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.course.entity.StaffEntity;
import com.course.model.StaffVo;
import com.course.repository.StaffRepository;

import jakarta.transaction.Transactional;

@Service
public class StaffService {

	@Autowired
	private StaffRepository staffRepository;
	
	@Autowired
	private StaffServiceHelper helper;
	
	public StaffVo staffLogin(String username,String password)
	{		
		StaffEntity staffEntity=staffRepository.findByUsernameAndPassword(username, password);
		if(staffEntity!=null)
		{
			return helper.convertToVo(staffEntity);
		}
		else 
		{
			return null;
		}
	}
	
	@Transactional
	public void addStaff(StaffVo vo)
	{
		StaffEntity staffEntity=new StaffEntity();
		staffEntity.setName(vo.getName());
		staffEntity.setUsername(vo.getUsername());
		staffEntity.setPassword(vo.getPassword());
		staffEntity.setRole(vo.getRole());
		
		staffRepository.save(staffEntity);
	}
}
