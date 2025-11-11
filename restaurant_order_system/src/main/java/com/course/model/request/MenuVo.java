package com.course.model.request;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MenuVo {

	private Long id;
	
	@NotBlank
	@NotNull
	private String name;
	
	private Short type;
	
	@NotNull
	private BigDecimal price;
	
	private String img;
	
	private MultipartFile image;
	
	private String description;
	
	@NotNull
	private Short status;
}
