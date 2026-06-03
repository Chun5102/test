package com.course.model.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "主訂單完整資料")
public class MainOrderAggregateResponse {

    @Schema(description = "桌號", example = "1")
    private Integer tableId;

    @Schema(description = "主訂單編號", example = "MO20260408001")
    private String code;

    @Schema(description = "主訂單總價", example = "100")
    private BigDecimal totalPrice;

    @Schema(description = "主訂單狀態", example = "已完成")
    private String mainOrderStatus;

    @Builder.Default
    private List<OrderResponse> batches = new ArrayList<>();
}