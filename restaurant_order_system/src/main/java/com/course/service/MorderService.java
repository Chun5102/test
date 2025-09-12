package com.course.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	public ApiResponse<String> addMorder(MorderVo vo) {
		if (!morderRepository.existsByCode(vo.getCode())) {
			MorderEntity morderEntity = new MorderEntity();
			morderEntity.setCode(vo.getCode());
			morderEntity.setTableNumber(vo.getTableNumber());
			morderEntity.setMorderStatus(vo.getMorderStatus());
			morderEntity.setDate(vo.getDate());
			morderEntity.setTotalPrice(vo.getTotalPrice());
			morderEntity.setPaymentStatus(vo.getPaymentStatus());

			morderRepository.save(morderEntity);

			vo.getMorderItem().stream().map(item -> {
				MorderItemEntity morderItemEntity = new MorderItemEntity();
				morderItemEntity.setMorderCode(vo.getCode());
				morderItemEntity.setMenuId(item.getMenuId());
				morderItemEntity.setQuantity(item.getQuantity());
				morderItemEntity.setSubtotal(item.getSubtotal());
				return morderItemEntity;
			}).forEach(morderItemRepository::save);

			return ApiResponse.success("訂單新增成功");
		} else {
			return ApiResponse.error("401", "已有此訂單");
		}
	}

	@Transactional
	public ApiResponse<String> updateMorder(MorderVo vo) {
		if (morderRepository.existsByCode(vo.getCode())) {
			MorderEntity morderEntity = morderRepository.findByCode(vo.getCode());
			morderEntity.setMorderStatus(vo.getMorderStatus());
			morderEntity.setTotalPrice(vo.getTotalPrice());
			morderEntity.setPaymentStatus(vo.getPaymentStatus());

			morderRepository.save(morderEntity);

			List<MorderItemEntity> morderItems = morderItemRepository.findByMorderCode(vo.getCode());
			Map<Long, MorderItemEntity> morderItemMap = morderItems.stream()
					.collect(Collectors.toMap(MorderItemEntity::getMenuId, item -> item));

			vo.getMorderItem().stream().map(item -> {
				MorderItemEntity morderItemEntity = morderItemMap.get(item.getMenuId());
				if (morderItemEntity == null) {
					morderItemEntity = new MorderItemEntity();
					morderItemEntity.setMenuId(item.getMenuId());
					morderItemEntity.setMorderCode(vo.getCode());
				}
				morderItemEntity.setQuantity(item.getQuantity());
				morderItemEntity.setSubtotal(item.getSubtotal());
				return morderItemEntity;
			}).forEach(morderItemRepository::save);
			return ApiResponse.success("訂單更新成功");
		} else {
			return ApiResponse.error("401", "無此訂單");
		}
	}

	@Transactional
	public ApiResponse<String> deleteMorder(String code) {
		MorderEntity morderEntity = morderRepository.findByCode(code);
		if (morderRepository.existsByCode(code)) {
			morderRepository.deleteByCode(code);
			List<MorderItemEntity> morderItemList = morderItemRepository.findByMorderCode(code);
			morderItemRepository.deleteAllInBatch(morderItemList);
			return ApiResponse.success("訂單刪除成功");
		} else {
			return ApiResponse.error("401", "無此訂單");
		}
	}

	public ApiResponse<List<MorderVo>> findByTableNumAndPayStatus(String tableNum) {
		List<MorderEntity> morderEntityList = morderRepository.findByTableNumberAndPaymentStatus(tableNum, (short) 0);
		Map<Long, List<MorderItemEntity>> morderItemMap = new HashMap<>();

		morderEntityList.stream().map(morderEntity -> {
			MorderVo vo = new MorderVo();
			vo.setCode(morderEntity.getCode());
			vo.setMorderStatus(morderEntity.getMorderStatus());
			vo.setTotalPrice(morderEntity.getTotalPrice());
//			vo.setMorderItem();
			return vo;
		}).collect(Collectors.toList());
		return ApiResponse.error("401", "無此訂單");
	}

}
