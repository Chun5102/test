package com.course.entity;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "morder")
@Data
public class MorderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String code;

	private Integer tableNumber;

	private String morderStatus;

	@Column(name = "date", insertable = false, updatable = false)
	private Date date;

	private BigDecimal totalPrice;

	private Short paymentStatus;
}
