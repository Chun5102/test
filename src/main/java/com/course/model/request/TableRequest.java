package com.course.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TableRequest {

    @NotNull
    @Schema(description = "桌子狀態", example = "空閒")
    private String status;

    @Schema(description = "是否清除開啟時間", example = "true")
    private Boolean clearOpenedAt;
}
