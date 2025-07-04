package com.zkyzn.project_manager.models.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 延期风险通知
 * @author Jiangsk
 */
@Getter
@Setter
public class DelayAlertContent extends BaseContent{

    @NotBlank(message = "受影响任务名称不能为空")
    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("task_name")
    private String taskName;

    @NotBlank(message = "当前进度")
    @Schema(description = "当前进度", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("current_progress")
    private String currentProgress;

    @NotBlank(message = "原计划进度")
    @Schema(description = "原计划进度", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("original_progress")
    private String originalProgress;

    @NotBlank(message = "告警原因")
    @Schema(description = "告警原因", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> reasons;

    @NotNull(message = "告警时间")
    @Schema(description = "告警时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("alert_time")
    private String alertTime;

}
