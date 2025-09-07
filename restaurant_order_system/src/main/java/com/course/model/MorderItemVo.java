package com.course.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class MorderItemVo {
	private Long menuId;
	
	private String name;
	
	private Integer quantity;
	
	private BigDecimal subtotal;
}
