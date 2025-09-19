package cn.master.hub.dto.system;

import cn.master.hub.dto.OptionDTO;
import cn.master.hub.entity.SystemUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OrgUserExtend extends SystemUser {
    @Schema(description =  "项目ID名称集合", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<OptionDTO> projectIdNameMap;;

    @Schema(description =  "用户组ID名称集合", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<OptionDTO> userRoleIdNameMap;
}
