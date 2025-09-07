package com.course.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.course.entity.MorderEntity;
import com.course.entity.MorderItemEntity;
import com.course.model.ApiResponse;
import com.course.model.MorderVo;
import com.course.repository.MorderItemRepository;
import com.course.repository.MorderRepository;

import jakarta.transaction.Transactional;

@Service
public class MorderService {

	@Autowired
	private MorderRepository morderRepository;
	
	@Autowired
	private MorderItemRepository morderItemRepository;
	
	@Autowired
	private ServiceHelper helper;
	
	@Transactional
	public ApiResponse<String> addMorder(MorderVo vo)
	{
		if(!morderRepository.existsByTableNumberAndDate(vo.getTableNumber(),vo.getDate()))
		{
			MorderEntity morderEntity=new MorderEntity();
			morderEntity.setTableNumber(vo.getTableNumber());
			morderEntity.setMorderStatus(vo.getMorderStatus());
			morderEntity.setDate(vo.getDate());
			morderEntity.setTotalPrice(vo.getTotalPrice());
			morderEntity.setPaymentStatus(vo.getPaymentStatus());
					
			morderRepository.save(morderEntity);
					
			vo.getMorderItem().stream()
	        .map(item -> {
	            MorderItemEntity morderItemEntity=new MorderItemEntity();
	            morderItemEntity.setMenuId(item.getMenuId());
	            morderItemEntity.setMorderId(morderEntity.getId());
	            morderItemEntity.setQuantity(item.getQuantity());
	            morderItemEntity.setSubtotal(item.getSubtotal());
	            return morderItemEntity;
	        })
	        .forEach(morderItemRepository::save);
			
			return ApiResponse.success("訂單新增成功");
		}
		else
		{
			return ApiResponse.error("401","已有此訂單");
		}
	}
}
