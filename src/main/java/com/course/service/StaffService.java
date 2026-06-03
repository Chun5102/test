package com.course.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.course.dao.StaffDao;
import com.course.entity.StaffEntity;
import com.course.enums.ResultCode;
import com.course.model.request.StaffRequest;
import com.course.model.request.StaffUpdateRequest;
import com.course.model.response.ApiResponse;
import com.course.model.response.StaffResponse;
import com.course.utils.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
public class StaffService {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private StaffDao staffDao;

	@Autowired
	private JwtUtil jwtUtil;

	public ApiResponse<String> staffLogin(HttpServletResponse response, String username, String password) {
		StaffEntity staffEntity = staffDao.getStaffDataByUsername(username);
		if (staffEntity != null
				&& staffEntity.getIsActive()
				&& passwordEncoder.matches(password, staffEntity.getPassword())) {
			String accessToken = jwtUtil.generateAccessToken(staffEntity);
			String refreshToken = jwtUtil.generateRefreshToken(staffEntity);

			ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
					.httpOnly(true)
					.secure(false)
					.path("/")
					.maxAge(60 * 60 * 24)
					.sameSite("Lax")
					.build();

			response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
			return ApiResponse.success(accessToken);
		} else {
			return ApiResponse.error(ResultCode.LOGIN_FAIL);
		}
	}

	public ApiResponse<String> staffLogout(HttpServletResponse response) {
		ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
				.httpOnly(true)
				.secure(false)
				.path("/")
				.maxAge(0)
				.sameSite("Lax")
				.build();
		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
		return ApiResponse.success("登出成功");
	}

	@Transactional
	public ApiResponse<String> addStaff(StaffRequest req) {
		if (!staffDao.existsByUsername(req.getUsername())) {
			StaffEntity staffEntity = new StaffEntity();
			staffEntity.setName(req.getName());
			staffEntity.setUsername(req.getUsername());
			staffEntity.setPassword(passwordEncoder.encode(req.getPassword()));
			staffEntity.setRole(req.getRole());
			staffEntity.setIsActive(true);

			staffDao.addStaff(staffEntity);

			return ApiResponse.success("員工新增成功");
		} else {
			return ApiResponse.error(ResultCode.STAFF_IS_EXIST);
		}
	}

	@Transactional
	public ApiResponse<String> updateStaff(StaffUpdateRequest req) {
		StaffEntity staffEntity = staffDao.findById(req.getId());
		if (staffEntity != null) {
			staffEntity.setName(req.getName());
			staffEntity.setRole(req.getRole());
			if (req.getResetPassword()) {
				staffEntity.setPassword(passwordEncoder.encode(req.getPassword()));
			}

			staffDao.updateStaff(staffEntity);
			return ApiResponse.success("員工修改成功");
		}
		return ApiResponse.error(ResultCode.STAFF_UPDATE_FAIL);
	}

	public ApiResponse<String> toggleStaffActive(Long id) {
		StaffEntity staffEntity = staffDao.findById(id);

		if (staffEntity != null) {
			Boolean isActive = staffEntity.getIsActive();

			staffEntity.setIsActive(!isActive);

			staffDao.updateStaff(staffEntity);

			return ApiResponse.success("員工" + (isActive ? "啟用" : "停用") + "成功");
		}
		return ApiResponse.error(ResultCode.STAFF_NOT_EXIST);
	}

	public ApiResponse<StaffResponse> getStaffById(Long id) {
		StaffEntity staffEntity = staffDao.findById(id);
		if (staffEntity != null) {
			return ApiResponse.success(staffConvertToVo(staffEntity));
		}
		return ApiResponse.error(ResultCode.STAFF_NOT_EXIST);
	}

	public ApiResponse<List<StaffResponse>> staffFindByName(String name) {
		List<StaffEntity> staffEntityList = staffDao.findByNameLike("%" + name + "%");
		if (!staffEntityList.isEmpty()) {
			return ApiResponse.success(staffEntityList.stream().map(staffEntity -> {
				return staffConvertToVo(staffEntity);
			}).collect(Collectors.toList()));
		}
		return ApiResponse.error(ResultCode.STAFF_NOT_EXIST);
	}

	public ApiResponse<List<StaffResponse>> getAllStaff() {
		List<StaffEntity> staffEntityList = staffDao.findAll();
		if (!staffEntityList.isEmpty()) {
			return ApiResponse.success(staffEntityList.stream().map(staffEntity -> {
				return staffConvertToVo(staffEntity);
			}).collect(Collectors.toList()));
		}
		return ApiResponse.error(ResultCode.STAFF_NOT_EXIST);
	}

	private StaffResponse staffConvertToVo(StaffEntity staffEntity) {
		StaffResponse vo = new StaffResponse();
		vo.setId(staffEntity.getId());
		vo.setName(staffEntity.getName());
		vo.setUsername(staffEntity.getUsername());
		vo.setRole(staffEntity.getRole());
		vo.setIsActive(staffEntity.getIsActive());

		return vo;
	}

}
