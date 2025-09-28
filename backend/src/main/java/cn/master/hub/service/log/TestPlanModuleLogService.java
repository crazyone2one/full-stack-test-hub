package cn.master.hub.service.log;

import cn.master.hub.dto.BaseModule;
import cn.master.hub.dto.NodeSortDTO;
import cn.master.hub.entity.SystemProject;
import cn.master.hub.entity.TestPlanModule;
import cn.master.hub.handler.Translator;
import cn.master.hub.handler.log.LogDTO;
import cn.master.hub.handler.log.LogDTOBuilder;
import cn.master.hub.handler.log.OperationLogModule;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.mapper.SystemProjectMapper;
import cn.master.hub.service.OperationLogService;
import cn.master.hub.util.JacksonUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * @author Created by 11's papa on 2025/9/28
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestPlanModuleLogService {
    private String logModule = OperationLogModule.TEST_PLAN_MODULE;

    @Resource
    private SystemProjectMapper projectMapper;
    @Resource
    private OperationLogService operationLogService;

    public void saveAddLog(TestPlanModule module, String operator, String requestUrl, String requestMethod) {
        SystemProject project = projectMapper.selectOneById(module.getProjectId());
        LogDTO dto = LogDTOBuilder.builder()
                .projectId(module.getProjectId())
                .organizationId(project.getOrganizationId())
                .type(OperationLogType.ADD.name())
                .module(logModule)
                .method(requestMethod)
                .path(requestUrl)
                .sourceId(module.getId())
                .content(module.getName())
                .originalValue(JacksonUtils.toJSONBytes(module))
                .createUser(operator)
                .build().getLogDTO();
        operationLogService.add(dto);
    }

    public void saveUpdateLog(TestPlanModule oldModule, TestPlanModule newModule, String projectId, String operator, String requestUrl, String requestMethod) {
        SystemProject project = projectMapper.selectOneById(projectId);
        LogDTO dto = LogDTOBuilder.builder()
                .projectId(projectId)
                .organizationId(project.getOrganizationId())
                .type(OperationLogType.UPDATE.name())
                .module(logModule)
                .method(requestMethod)
                .path(requestUrl)
                .sourceId(newModule.getId())
                .content(newModule.getName())
                .originalValue(JacksonUtils.toJSONBytes(oldModule))
                .modifiedValue(JacksonUtils.toJSONBytes(newModule))
                .createUser(operator)
                .build().getLogDTO();
        operationLogService.add(dto);
    }

    public void saveDeleteLog(TestPlanModule deleteModule, String operator, String requestUrl, String requestMethod) {
        SystemProject project = projectMapper.selectOneById(deleteModule.getProjectId());
        LogDTO dto = LogDTOBuilder.builder()
                .projectId(deleteModule.getProjectId())
                .organizationId(project.getOrganizationId())
                .type(OperationLogType.DELETE.name())
                .module(logModule)
                .method(requestMethod)
                .path(requestUrl)
                .sourceId(deleteModule.getId())
                .content(deleteModule.getName() + " " + Translator.get("log.delete_module"))
                .originalValue(JacksonUtils.toJSONBytes(deleteModule))
                .createUser(operator)
                .build().getLogDTO();
        operationLogService.add(dto);
    }

    public void saveMoveLog(@Validated NodeSortDTO request, String operator, String requestUrl, String requestMethod) {
        BaseModule moveNode = request.getNode();
        BaseModule previousNode = request.getPreviousNode();
        BaseModule nextNode = request.getNextNode();
        BaseModule parentModule = request.getParent();

        SystemProject project = projectMapper.selectOneById(moveNode.getProjectId());
        String logContent;
        if (nextNode == null && previousNode == null) {
            logContent = moveNode.getName() + " " + Translator.get("file.log.move_to") + parentModule.getName();
        } else if (nextNode == null) {
            logContent = moveNode.getName() + " " + Translator.get("file.log.move_to") + parentModule.getName() + " " + previousNode.getName() + Translator.get("file.log.next");
        } else if (previousNode == null) {
            logContent = moveNode.getName() + " " + Translator.get("file.log.move_to") + parentModule.getName() + " " + nextNode.getName() + Translator.get("file.log.previous");
        } else {
            logContent = moveNode.getName() + " " + Translator.get("file.log.move_to") + parentModule.getName() + " " +
                    previousNode.getName() + Translator.get("file.log.next") + " " + nextNode.getName() + Translator.get("file.log.previous");
        }
        LogDTO dto = LogDTOBuilder.builder()
                .projectId(moveNode.getProjectId())
                .organizationId(project.getOrganizationId())
                .type(OperationLogType.UPDATE.name())
                .module(logModule)
                .method(requestMethod)
                .path(requestUrl)
                .sourceId(moveNode.getId())
                .content(logContent)
                .createUser(operator)
                .build().getLogDTO();
        operationLogService.add(dto);
    }
}
