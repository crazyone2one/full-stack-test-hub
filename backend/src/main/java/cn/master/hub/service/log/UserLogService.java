package cn.master.hub.service.log;

import cn.master.hub.constants.HttpMethodConstants;
import cn.master.hub.constants.OperationLogConstants;
import cn.master.hub.dto.UserCreateInfo;
import cn.master.hub.dto.request.UserEditRequest;
import cn.master.hub.dto.system.TableBatchProcessDTO;
import cn.master.hub.dto.system.request.UserChangeEnableRequest;
import cn.master.hub.entity.SystemUser;
import cn.master.hub.handler.Translator;
import cn.master.hub.handler.log.LogDTO;
import cn.master.hub.handler.log.LogDTOBuilder;
import cn.master.hub.handler.log.OperationLogModule;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.mapper.SystemUserMapper;
import cn.master.hub.service.UserToolService;
import cn.master.hub.util.JacksonUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/4
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserLogService {
    @Resource
    SystemUserMapper userMapper;
    @Resource
    UserToolService userToolService;

    public List<LogDTO> getBatchAddLogs(@Valid List<UserCreateInfo> userList, String operator, String requestPath) {
        List<LogDTO> logs = new ArrayList<>();
        userList.forEach(user -> {
            LogDTO log = LogDTOBuilder.builder()
                    .projectId(OperationLogConstants.SYSTEM)
                    .organizationId(OperationLogConstants.SYSTEM)
                    .type(OperationLogType.ADD.name())
                    .module(OperationLogModule.SETTING_SYSTEM_USER_SINGLE)
                    .method(HttpMethodConstants.POST.name())
                    .path(requestPath)
                    .sourceId(user.getId())
                    .content(user.getName() + "(" + user.getEmail() + ")")
                    .originalValue(JacksonUtils.toJSONBytes(user))
                    .createUser(operator)
                    .build().getLogDTO();
            logs.add(log);
        });
        return logs;
    }

    public LogDTO updateLog(UserEditRequest request) {
        SystemUser user = userMapper.selectOneById(request.getId());
        if (user != null) {
            return LogDTOBuilder.builder()
                    .projectId(OperationLogConstants.SYSTEM)
                    .organizationId(OperationLogConstants.SYSTEM)
                    .type(OperationLogType.UPDATE.name())
                    .module(OperationLogModule.SETTING_SYSTEM_USER_SINGLE)
                    .method(HttpMethodConstants.POST.name())
                    .path("/system/user/update")
                    .sourceId(request.getId())
                    .content(user.getName())
                    .originalValue(JacksonUtils.toJSONBytes(user))
                    .build().getLogDTO();
        }
        return null;
    }

    public List<LogDTO> batchUpdateEnableLog(UserChangeEnableRequest request) {
        List<LogDTO> logDTOList = new ArrayList<>();
        request.setSelectIds(userToolService.getBatchUserIds(request));
        List<SystemUser> userList = userMapper.selectListByIds(request.getSelectIds());
        for (SystemUser user : userList) {
            LogDTO dto = LogDTOBuilder.builder()
                    .projectId(OperationLogConstants.SYSTEM)
                    .organizationId(OperationLogConstants.SYSTEM)
                    .type(OperationLogType.UPDATE.name())
                    .module(OperationLogModule.SETTING_SYSTEM_USER_SINGLE)
                    .method(HttpMethodConstants.POST.name())
                    .path("/system/user/update/enable")
                    .sourceId(user.getId())
                    .content((request.isEnable() ? Translator.get("user.enable") : Translator.get("user.disable")) + ":" + user.getName())
                    .originalValue(JacksonUtils.toJSONBytes(user))
                    .build().getLogDTO();
            logDTOList.add(dto);
        }
        return logDTOList;
    }

    public List<LogDTO> resetPasswordLog(TableBatchProcessDTO request) {
        request.setSelectIds(userToolService.getBatchUserIds(request));
        List<LogDTO> returnList = new ArrayList<>();
        List<SystemUser> userList = userMapper.selectListByIds(request.getSelectIds());
        for (SystemUser user : userList) {
            LogDTO dto = LogDTOBuilder.builder()
                    .projectId(OperationLogConstants.SYSTEM)
                    .organizationId(OperationLogConstants.SYSTEM)
                    .type(OperationLogType.UPDATE.name())
                    .module(OperationLogModule.SETTING_SYSTEM_USER_SINGLE)
                    .method(HttpMethodConstants.POST.name())
                    .path("/system/user/reset/password")
                    .sourceId(user.getId())
                    .content(Translator.get("user.reset.password") + " : " + user.getName())
                    .originalValue(JacksonUtils.toJSONBytes(user))
                    .build().getLogDTO();
            returnList.add(dto);
        }
        return returnList;
    }

    public List<LogDTO> deleteLog(TableBatchProcessDTO request) {
        List<LogDTO> logDTOList = new ArrayList<>();
        request.getSelectIds().forEach(item -> {
            SystemUser user = userMapper.selectOneById(item);
            if (user != null) {
                LogDTO dto = LogDTOBuilder.builder()
                        .projectId(OperationLogConstants.SYSTEM)
                        .organizationId(OperationLogConstants.SYSTEM)
                        .type(OperationLogType.DELETE.name())
                        .module(OperationLogModule.SETTING_SYSTEM_USER_SINGLE)
                        .method(HttpMethodConstants.POST.name())
                        .path("/system/user/delete")
                        .sourceId(user.getId())
                        .content(user.getName())
                        .originalValue(JacksonUtils.toJSONBytes(user))
                        .build().getLogDTO();
                logDTOList.add(dto);

            }
        });
        return logDTOList;
    }
}
