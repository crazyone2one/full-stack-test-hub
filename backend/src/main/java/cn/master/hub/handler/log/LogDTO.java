package cn.master.hub.handler.log;

import cn.master.hub.entity.OperationLog;

/**
 * @author Created by 11's papa on 2025/9/10
 */
public class LogDTO extends OperationLog {
    public LogDTO(String projectId, String organizationId, String sourceId, String createUser, String type, String module, String content) {
        this.setProjectId(projectId);
        this.setOrganizationId(organizationId);
        this.setSourceId(sourceId);
        this.setCreateUser(createUser);
        this.setType(type);
        this.setModule(module);
        this.setContent(content);
    }
    public LogDTO() {
    }
}
