package cn.master.hub.dto.response;

import cn.master.hub.dto.system.ProjectResourcePoolDTO;
import cn.master.hub.dto.system.UserExtendDTO;
import cn.master.hub.entity.SystemProject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/1
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProjectDTO extends SystemProject {
    @Schema(description = "项目成员数量", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long memberCount;
    @Schema(description = "所属组织", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String organizationName;
    @Schema(description = "管理员", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<UserExtendDTO> adminList;
    @Schema(description = "创建人是否是管理员", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean projectCreateUserIsAdmin;
//    @Schema(description = "模块设置", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
//    private List<String> moduleIds;
    @Schema(description = "资源池", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ProjectResourcePoolDTO> resourcePoolList;
    @Schema(description = "剩余删除保留天数")
    private Integer remainDayCount;
}
