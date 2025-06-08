package com.zkyzn.project_manager.so.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zkyzn.project_manager.models.MessageInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "查询消息请求")
public class MsgReq extends MessageInfo {

    @Schema(description = "用户Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("userId")
    private String userId;

}
