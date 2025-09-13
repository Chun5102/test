package com.course.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class MorderVo {

	@Column(unique = true)
	private String code;

	private Integer tableNumber;

	private String morderStatus;

	private Date date;

	private BigDecimal totalPrice;

	private Short paymentStatus;

	private List<MorderItemVo> morderItem = new ArrayList<>();

	public MorderVo() {
		super();
		this.morderItem = new ArrayList<>();
	}

	public MorderVo(String code, String morderStatus, BigDecimal totalPrice) {
		super();
		this.code = code;
		this.morderStatus = morderStatus;
		this.totalPrice = totalPrice;
	}

}
