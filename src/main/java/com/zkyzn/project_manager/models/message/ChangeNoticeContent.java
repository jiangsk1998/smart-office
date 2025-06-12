package com.zkyzn.project_manager.models.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * 变更通知
 * @author Jiangsk
 */
@Getter
@Setter
public class ChangeNoticeContent extends BaseContent{

    @NotBlank(message = "项目工号不能为空")
    @Schema(description = "项目工号", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("project_number")
    private String projectNumber;

    @NotBlank(message = "项目名称不能为空")
    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("project_name")
    private String projectName;

    @NotNull(message = "立项时间不能为空")
    @Schema(description = "立项时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("start_date")
    private String startDate;

    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("end_date")
    private String endDate;

    @NotBlank(message = "项目当前阶段不能为空")
    @Schema(description = "项目当前阶段", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("current_phase")
    private String currentPhase;

    @NotBlank(message = "变更内容不能为空")
    @Schema(description = "变更内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
}
