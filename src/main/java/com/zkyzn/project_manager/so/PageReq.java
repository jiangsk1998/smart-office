package com.zkyzn.project_manager.so;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Jiangsk
 */
@Data
public  class PageReq {
    @Schema(description = "当前页码", defaultValue = "1")
    private long current = 1;
    
    @Schema(description = "每页条数", defaultValue = "10")
    private long size = 10;
}