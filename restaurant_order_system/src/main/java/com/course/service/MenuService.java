package com.course.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.course.entity.MenuEntity;
import com.course.model.ApiResponse;
import com.course.model.MenuVo;
import com.course.repository.MenuRepository;

import jakarta.transaction.Transactional;

@Service
public class MenuService {

	private static final String UPLOAD_DIR = "/Users/evera/static";

	@Autowired
	private MenuRepository menuRepository;

	@Autowired
	private ServiceHelper helper;

	@Transactional
	public ApiResponse<String> addMenu(MenuVo vo) throws IOException {
		if (!menuRepository.existsByName(vo.getName())) {
			MenuEntity menuEntity = new MenuEntity();
			menuEntity.setName(vo.getName());
			menuEntity.setType(vo.getType());
			menuEntity.setPrice(vo.getPrice());
			menuEntity.setDescription(vo.getDescription());
			menuEntity.setStatus(vo.getStatus());

			menuRepository.save(menuEntity);

			if (vo.getImage() != null) {
				menuEntity.setImg(saveImage(vo.getImage(), menuEntity.getId()));
			}

			menuRepository.save(menuEntity);

			return ApiResponse.success("菜單新增成功");
		} else {
			return ApiResponse.error("401", "已有此菜單");
		}
	}

	@Transactional
	public ApiResponse<String> updateMenu(MenuVo vo) throws IOException {
		Optional<MenuEntity> menuEntityOp = menuRepository.findById(vo.getId());
		if (menuEntityOp.isPresent()) {
			MenuEntity menuEntity = menuEntityOp.get();
			menuEntity.setName(vo.getName());
			menuEntity.setType(vo.getType());
			menuEntity.setPrice(vo.getPrice());
			menuEntity.setDescription(vo.getDescription());
			menuEntity.setStatus(vo.getStatus());
			if (vo.getImg() == null && vo.getImage() != null) {
				menuEntity.setImg(saveImage(vo.getImage(), vo.getId()));
			} else if (vo.getImage() != null) {
				saveImage(vo.getImage(), vo.getId());
			}

			menuRepository.save(menuEntity);

			return ApiResponse.success("菜單更新成功");
		} else {
			return ApiResponse.error("401", "無此菜單");
		}
	}

	public ApiResponse<String> deleteMenu(Long id) {
		Optional<MenuEntity> menuEntityOp = menuRepository.findById(id);
		if (menuEntityOp.isPresent()) {
			String filePath = UPLOAD_DIR + "/" + menuEntityOp.get().getImg();
			File file = new File(filePath);
			file.delete();

//			menuRepository.deleteById(id);
			MenuEntity menuEntity = menuEntityOp.get();
			menuEntity.setStatus((short) 4);
			menuRepository.save(menuEntity);

			return ApiResponse.success("菜單刪除成功");
		} else {
			return ApiResponse.error("401", "無此菜單");
		}
	}

	public ApiResponse<MenuVo> menuFindById(Long id) {
		Optional<MenuEntity> menuEntityOp = menuRepository.findById(id);
		if (menuEntityOp.isPresent()) {
			return ApiResponse.success(helper.menuConvertToVo(menuEntityOp.get()));
		}
		return ApiResponse.error("401", "搜索失敗");
	}

	public ApiResponse<List<MenuVo>> menuFindByName(String name) {
		List<MenuEntity> menuEntityList = menuRepository.findByNameLike("%" + name + "%");
		if (!menuEntityList.isEmpty()) {
			return ApiResponse.success(menuEntityList.stream().map(menuEntity -> {
				return helper.menuConvertToVo(menuEntity);
			}).collect(Collectors.toList()));
		}
		return ApiResponse.error("401", "搜索失敗");
	}

	public ApiResponse<List<MenuVo>> menuFindByType(Short type) {
		List<MenuEntity> menuEntityList = menuRepository.findByType(type);
		if (!menuEntityList.isEmpty()) {
			return ApiResponse.success(menuEntityList.stream().map(menuEntity -> {
				return helper.menuConvertToVo(menuEntity);
			}).collect(Collectors.toList()));
		}
		return ApiResponse.error("401", "搜索失敗");
	}

	// 儲存圖片 回傳圖片名
	private String saveImage(MultipartFile file, Long id) throws IOException {
		String originalFileName = file.getOriginalFilename();

		String newFileName = "menu_" + id + getFileExtension(originalFileName);

		Path uploadPath = Paths.get(UPLOAD_DIR);

		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		Path filePath = uploadPath.resolve(newFileName);

		Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

		return newFileName;
	}

	// 回傳圖片副檔名
	private String getFileExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf(".");

		if (dotIndex >= 0) {
			return fileName.substring(dotIndex);
		}

		return "";
	}
}
