package com.zkyzn.project_manager.so;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Result<T> {
    @Schema(description = "是否错误")
    private Boolean ok;

    @Schema(description = "错误信息")
    private Err err;

    @Schema(description = "数据")
    private T data;

    public Result<T> withErrCode(Integer code) {
        if(err != null){
            err.setCode(code);
        }
        return this;
    }
}
