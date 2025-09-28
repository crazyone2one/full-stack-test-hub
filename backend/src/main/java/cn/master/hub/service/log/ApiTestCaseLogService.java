package cn.master.hub.service.log;

import cn.master.hub.constants.HttpMethodConstants;
import cn.master.hub.dto.api.ApiTestCaseAddRequest;
import cn.master.hub.entity.ApiTestCase;
import cn.master.hub.entity.SystemProject;
import cn.master.hub.handler.log.LogDTO;
import cn.master.hub.handler.log.OperationLogModule;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.mapper.ApiTestCaseMapper;
import cn.master.hub.mapper.SystemProjectMapper;
import cn.master.hub.service.OperationLogService;
import cn.master.hub.util.JacksonUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/26
 */
@Service
public class ApiTestCaseLogService {
    @Resource
    private SystemProjectMapper projectMapper;
    @Resource
    private ApiTestCaseMapper apiTestCaseMapper;
    @Resource
    private OperationLogService operationLogService;

    public LogDTO addLog(ApiTestCaseAddRequest request) {
        SystemProject project = projectMapper.selectOneById(request.getProjectId());
        LogDTO dto = new LogDTO(
                request.getProjectId(),
                project.getOrganizationId(),
                null,
                null,
                OperationLogType.ADD.name(),
                OperationLogModule.API_TEST_MANAGEMENT_CASE,
                request.getName());
        dto.setMethod(HttpMethodConstants.POST.name());
        dto.setOriginalValue(JacksonUtils.toJSONBytes(request));
        dto.setHistory(true);
        return dto;
    }

    public LogDTO deleteLog(String id) {
        ApiTestCase apiTestCase = apiTestCaseMapper.selectOneById(id);
        SystemProject project = projectMapper.selectOneById(apiTestCase.getProjectId());
        LogDTO dto = new LogDTO(
                apiTestCase.getProjectId(),
                project.getOrganizationId(),
                id,
                null,
                OperationLogType.DELETE.name(),
                OperationLogModule.API_TEST_MANAGEMENT_RECYCLE,
                apiTestCase.getName());
        dto.setMethod(HttpMethodConstants.GET.name());
        dto.setOriginalValue(JacksonUtils.toJSONBytes(apiTestCase));
        operationLogService.deleteBySourceIds(List.of(id));
        return dto;
    }
}
