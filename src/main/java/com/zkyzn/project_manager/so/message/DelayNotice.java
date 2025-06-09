package com.zkyzn.project_manager.so.message;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 延期风险通知
 * @author Jiangsk
 */
@Getter
@Setter
public class DelayNotice extends BaseContent{

    @NotBlank(message = "项目工号不能为空")
    @Schema(description = "项目工号", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("projectNumber")
    private String projectNumber;

    @NotBlank(message = "项目名称不能为空")
    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("projectName")
    private String projectName;

    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("startDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("endDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotBlank(message = "项目当前阶段不能为空")
    @Schema(description = "项目当前阶段", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("currentPhase")
    private String currentPhase;

}
