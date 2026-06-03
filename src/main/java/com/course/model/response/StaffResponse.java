package com.course.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "員工")
public class StaffResponse {

    @Schema(description = "員工ID")
    private Long id;

    @Schema(description = "員工名字")
    private String name;

    @Schema(description = "員工帳號")
    private String username;

    @Schema(description = "員工角色")
    private String role;

    @Schema(description = "員工狀態")
    private Boolean isActive;
}
