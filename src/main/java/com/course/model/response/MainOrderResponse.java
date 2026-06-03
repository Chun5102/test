package com.course.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "主訂單列表資料")
public class MainOrderResponse {

    @Schema(description = "桌號", example = "1")
    private Integer tableId;

    @Schema(description = "主訂單編號", example = "ffhgs1ghdhd1f...")
    private String code;

    @Schema(description = "主訂單總價", example = "100")
    private BigDecimal totalPrice;

    @Schema(description = "主訂單狀態", example = "已完成")
    private String mainOrderStatus;

    @Schema(description = "主訂單付款狀態", example = "未付款")
    private String paymentStatus;

    @Schema(description = "主訂單付款方式", example = "現金")
    private String paymentMethod;

    @Schema(description = "主訂單付款金額", example = "100")
    private BigDecimal paidAmount;

    @Schema(description = "主訂單找零", example = "10")
    private BigDecimal changeAmount;

    @Schema(description = "主訂單付款時間", example = "2026-03-27T05:52:42.811878")
    private LocalDateTime paidAt;

    @Schema(description = "主訂單狀態", example = "true")
    private Boolean isActive;
}
