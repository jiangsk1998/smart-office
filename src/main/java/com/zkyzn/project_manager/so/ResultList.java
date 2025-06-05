package com.zkyzn.project_manager.so;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResultList<T> {
    @Schema(description = "是否错误")
    private Boolean ok;

    @Schema(description = "错误信息")
    private Err err;

    @Schema(description = "页码")
    private Integer pageNo;

    @Schema(description = "页大小")
    private Integer pageSize;

    @Schema(description = "数据总数")
    private Integer dataTotal;

    @Schema(description = "数据")
    private List<T> data;
}
