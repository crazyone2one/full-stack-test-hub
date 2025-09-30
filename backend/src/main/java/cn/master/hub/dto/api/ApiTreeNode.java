package cn.master.hub.dto.api;

import cn.master.hub.dto.BaseTreeNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Created by 11's papa on 2025/9/29
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ApiTreeNode extends BaseTreeNode {
    @Schema(description = "方法")
    private String method;
    @Schema(description = "协议")
    private String protocol;

}
