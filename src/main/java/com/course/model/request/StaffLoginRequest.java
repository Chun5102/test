package com.course.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "員工登入請求")
public class StaffLoginRequest {
    @NotNull
    @Schema(description = "員工帳號")
    private String username;
    @Schema(description = "員工密碼")
    private String password;
}
