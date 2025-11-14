package com.course.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.course.entity.MenuEntity;
import com.course.enums.MenuStatus;
import com.course.model.request.MenuRequest;
import com.course.model.request.MenuVo;
import com.course.model.response.ApiResponse;
import com.course.repository.MenuRepository;

import jakarta.transaction.Transactional;

@Service
public class MenuService {

	@Autowired
	private MenuRepository menuRepository;

	@Autowired
	private ServiceHelper helper;

	@Transactional
	public ApiResponse addMenu(MenuRequest req) throws IOException {

		ImageInfo imageInfo = processBase64Image(req.getImageBase64(), req.getImageType());

		MenuEntity menuEntity = MenuEntity.builder()
				.name(req.getName())
				.type(req.getType())
				.price(req.getPrice())
				.description(req.getDescription())
				.stock(req.getStock())
				.status(MenuStatus.ONSALE.getCode())
				.imageData(imageInfo.imageData())
				.imageType(imageInfo.imageType())
				.build();

		menuRepository.save(menuEntity);

	}

	@Transactional
	public ApiResponse updateMenu(Long id, MenuRequest req) throws IOException {
		MenuEntity menuEntity = menuRepository.findById(id).orElse(null);
		if (menuEntity != null) {

			ImageInfo imageInfo = processBase64Image(req.getImageBase64(), req.getImageType());
			MenuEntity updateMenuEntity = MenuEntity.builder()
					.id(menuEntity.getId())
					.name(req.getName())
					.type(req.getType())
					.price(req.getPrice())
					.description(req.getDescription())
					.stock(req.getStock())
					.status(req.getStatus())
					.imageData(imageInfo.imageData())
					.imageType(imageInfo.imageType())
					.build();

			menuRepository.save(updateMenuEntity);

			return ApiResponse.success();
		} else {
			return ApiResponse.error("401", "無此菜單");
		}
	}

	public ApiResponse deleteMenu(Long id) {
		MenuEntity menuEntity = menuRepository.findById(id).orElse(null);
		if (menuEntity != null) {

			menuEntity.setStatus(MenuStatus.DELETE.getCode());

			menuRepository.save(menuEntity);

			return ApiResponse.success();
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

	/**
	 * 產生圖片 Base64 字串
	 * 
	 * @param imageData 圖片資料
	 * @param imageType 圖片類型
	 * @return
	 */
	private String generateImageBase64(byte[] imageData, String imageType) {
		return imageData != null && imageType != null
				? "data:" + imageType + ";base64,"
						+ Base64.getEncoder().encodeToString(imageData)
				: null;
	}

	/**
	 * 用來傳遞圖片處理結果的 record。 Record 是 Java 14+ 的特性，適合用來傳遞不可變的資料物件。
	 */
	private record ImageInfo(byte[] imageData, String imageType) {
	}

	/**
	 * 處理 Base64 圖片字串，解析出圖片二進制資料和類型。
	 * 
	 * @param base64String      Base64 編碼的圖片字串，可包含 Data URI 前綴。
	 * @param existingImageType 已知或預設的圖片類型。
	 * @return 包含圖片資料和類型的 ImageInfo 物件。
	 */
	private ImageInfo processBase64Image(String base64String, String existingImageType) {
		if (base64String == null || base64String.isBlank()) {
			return new ImageInfo(null, null); // 沒有圖片，返回空值
		}

		String imageType = existingImageType;
		String base64Content = base64String;

		// 移除 Data URI scheme 前綴並嘗試解析圖片類型
		if (base64String.startsWith("data:")) {
			int commaIndex = base64String.indexOf(',');
			if (commaIndex != -1) {
				String dataUri = base64String.substring(0, commaIndex);
				if (dataUri.contains(";base64")) {
					imageType = dataUri.substring(dataUri.indexOf(':') + 1,
							dataUri.indexOf(';'));
				}
				base64Content = base64String.substring(commaIndex + 1);
			}
		}

		try {
			byte[] imageBytes = Base64.getDecoder().decode(base64Content);
			return new ImageInfo(imageBytes, imageType);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("無效的 Base64 圖片格式", e);
		}
	}
}
