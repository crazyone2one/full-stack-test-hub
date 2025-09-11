package cn.master.hub.service.log;

import cn.master.hub.constants.HttpMethodConstants;
import cn.master.hub.constants.OperationLogConstants;
import cn.master.hub.dto.request.AddProjectRequest;
import cn.master.hub.dto.system.UpdateProjectRequest;
import cn.master.hub.entity.OperationLog;
import cn.master.hub.entity.SystemProject;
import cn.master.hub.handler.log.LogDTO;
import cn.master.hub.handler.log.OperationLogModule;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.mapper.SystemProjectMapper;
import cn.master.hub.util.JacksonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Created by 11's papa on 2025/9/10
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class OrganizationProjectLogService {
    private final SystemProjectMapper projectMapper;

    public OperationLog addLog(AddProjectRequest project) {
        OperationLog dto = OperationLog.builder()
                .projectId(OperationLogConstants.ORGANIZATION)
                .organizationId(project.getOrganizationId())
                .sourceId(null).createUser(null)
                .type(OperationLogType.ADD.name())
                .module(OperationLogModule.SETTING_ORGANIZATION_PROJECT)
                .content(project.getName())
                .build();

        dto.setOriginalValue(JacksonUtils.toJSONBytes(project));
        return dto;
    }

    public LogDTO updateLog(UpdateProjectRequest request) {
        SystemProject project = projectMapper.selectOneById(request.getId());
        if (project != null) {
            LogDTO dto = new LogDTO(
                    OperationLogConstants.ORGANIZATION,
                    project.getOrganizationId(),
                    project.getId(),
                    null,
                    OperationLogType.UPDATE.name(),
                    OperationLogModule.SETTING_ORGANIZATION_PROJECT,
                    request.getName());

            dto.setOriginalValue(JacksonUtils.toJSONBytes(project));
            return dto;
        }
        return null;
    }

    public LogDTO updateLog(String id) {
        SystemProject project = projectMapper.selectOneById(id);
        if (project != null) {
            LogDTO dto = new LogDTO(
                    OperationLogConstants.ORGANIZATION,
                    project.getOrganizationId(),
                    project.getId(),
                    null,
                    OperationLogType.UPDATE.name(),
                    OperationLogModule.SETTING_ORGANIZATION_PROJECT,
                    project.getName());
            dto.setMethod(HttpMethodConstants.GET.name());

            dto.setOriginalValue(JacksonUtils.toJSONBytes(project));
            return dto;
        }
        return null;
    }

    public LogDTO deleteLog(String id) {
        SystemProject project = projectMapper.selectOneById(id);
        if (project != null) {
            LogDTO dto = new LogDTO(
                    OperationLogConstants.ORGANIZATION,
                    project.getOrganizationId(),
                    id,
                    null,
                    OperationLogType.DELETE.name(),
                    OperationLogModule.SETTING_ORGANIZATION_PROJECT,
                    project.getName());

            dto.setOriginalValue(JacksonUtils.toJSONBytes(project));
            return dto;
        }
        return null;
    }
}
