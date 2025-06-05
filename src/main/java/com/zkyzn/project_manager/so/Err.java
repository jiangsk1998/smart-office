package com.zkyzn.project_manager.so;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Err {

    @Schema(description = "错误码")
    private Integer code;

    @Schema(description = "错误信息")
    private String msg;
}
