package com.course.model.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "訂單付款請求")
public class PayOrderRequest {

	@NotNull
	@Schema(description = "訂單付款方式", example = "現金")
	private String paymentMethod;

	@NotNull
	@Schema(description = "訂單實際付款金額", example = "100")
	private BigDecimal paidAmount;

	@NotNull
	@Schema(description = "訂單找零", example = "10")
	private BigDecimal changeAmount;
}
