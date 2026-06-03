package com.course.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "員工")
public class StaffUpdateRequest {

    @Schema(description = "員工ID")
    private Long id;

    @Schema(description = "員工名字")
    private String name;

    @Schema(description = "員工密碼")
    private String password;

    @Schema(description = "員工角色")
    private String role;

    @Schema(description = "重設密碼")
    private Boolean resetPassword;
}
