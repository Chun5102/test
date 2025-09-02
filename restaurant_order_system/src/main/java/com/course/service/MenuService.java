package com.course.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.course.entity.MenuEntity;
import com.course.entity.StaffEntity;
import com.course.model.ApiResponse;
import com.course.model.MenuVo;
import com.course.repository.MenuRepository;

import jakarta.transaction.Transactional;

@Service
public class MenuService {

	private static final String UPLOAD_DIR = "/Users/static";
	
	@Autowired
	private MenuRepository menuRepository;
	
	@Transactional
	public ApiResponse<String> addMenu(MenuVo vo,MultipartFile image) throws IOException
	{
		if(!menuRepository.existsByName(vo.getName()))
		{
			MenuEntity menuEntity=new MenuEntity();
			menuEntity.setName(vo.getName());
			menuEntity.setType(vo.getType());
			menuEntity.setPrice(vo.getPrice());
			menuEntity.setDescription(vo.getDescription());
			menuEntity.setStatus(vo.getStatus());
			
			menuRepository.save(menuEntity);
			
			menuEntity.setImg(saveImage(image,menuEntity.getId()));
			
			menuRepository.save(menuEntity);
			
			return ApiResponse.success("菜單新增成功");
		}
		else
		{
			return ApiResponse.error("401","已有此菜單");
		}
	}
	
	private String saveImage(MultipartFile file,Long id) throws IOException 
	{
		String originalFileName=file.getOriginalFilename();
	    
	    String newFileName="menu_" + id + getFileExtension(originalFileName);
		
	    Path uploadPath=Paths.get(UPLOAD_DIR);
		
		if (!Files.exists(uploadPath)) 
		{
			Files.createDirectories(uploadPath);
		}
		
		Path filePath = uploadPath.resolve(newFileName);
		
		Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
		
		return newFileName;
	}
	
	private String getFileExtension(String fileName) 
	{
	    int dotIndex=fileName.lastIndexOf(".");
	    
	    if (dotIndex>=0) {
	        return fileName.substring(dotIndex);
	    }
	    
	    return "";  
	}
}
