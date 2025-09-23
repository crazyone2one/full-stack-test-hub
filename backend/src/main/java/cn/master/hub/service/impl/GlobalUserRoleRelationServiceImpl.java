package cn.master.hub.service.impl;

import cn.master.hub.constants.UserRoleScope;
import cn.master.hub.dto.system.TableBatchProcessResponse;
import cn.master.hub.dto.system.request.GlobalUserRoleRelationUpdateRequest;
import cn.master.hub.dto.system.request.UserRoleBatchRelationRequest;
import cn.master.hub.entity.UserRole;
import cn.master.hub.entity.UserRoleRelation;
import cn.master.hub.handler.Translator;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.mapper.SystemUserMapper;
import cn.master.hub.service.GlobalUserRoleRelationService;
import cn.master.hub.service.GlobalUserRoleService;
import cn.master.hub.service.OperationLogService;
import cn.master.hub.service.UserToolService;
import jakarta.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.master.hub.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;

/**
 * @author Created by 11's papa on 2025/9/23
 */
@Service("globalUserRoleRelationService")
public class GlobalUserRoleRelationServiceImpl extends BaseUserRoleRelationServiceImpl implements GlobalUserRoleRelationService {
    private final GlobalUserRoleService globalUserRoleService;
    private final UserToolService userToolService;
    private final SystemUserMapper userMapper;

    public GlobalUserRoleRelationServiceImpl(OperationLogService operationLogService,
                                             GlobalUserRoleService globalUserRoleService,
                                             UserToolService userToolService,
                                             SystemUserMapper userMapper) {
        super(operationLogService);
        this.globalUserRoleService = globalUserRoleService;
        this.userToolService = userToolService;
        this.userMapper = userMapper;
    }

    @Override
    public TableBatchProcessResponse batchAdd(UserRoleBatchRelationRequest request, String operator) {
        //检查角色的合法性
        checkGlobalSystemUserRoleLegality(request.getRoleIds());
        //获取本次处理的用户
        request.setSelectIds(userToolService.getBatchUserIds(request));
        //检查用户的合法性
        checkUserLegality(request.getSelectIds());
        List<UserRoleRelation> savedUserRoleRelation = selectByUserIdAndRuleId(request.getSelectIds(), request.getRoleIds());
        //过滤已经存储过的用户关系
        Map<String, List<String>> userRoleIdMap = savedUserRoleRelation.stream()
                .collect(Collectors.groupingBy(UserRoleRelation::getUserId, Collectors.mapping(UserRoleRelation::getRoleId, Collectors.toList())));
        List<UserRoleRelation> saveList = new ArrayList<>();
        for (String userId : request.getSelectIds()) {
            for (String roleId : request.getRoleIds()) {
                if (userRoleIdMap.containsKey(userId) && userRoleIdMap.get(userId).contains(roleId)) {
                    continue;
                }
                UserRoleRelation userRoleRelation = new UserRoleRelation();
                userRoleRelation.setUserId(userId);
                userRoleRelation.setRoleId(roleId);
                userRoleRelation.setCreateUser(operator);
                userRoleRelation.setSourceId(UserRoleScope.SYSTEM);
                userRoleRelation.setOrganizationId(UserRoleScope.SYSTEM);
                saveList.add(userRoleRelation);
            }
        }
        if (CollectionUtils.isNotEmpty(saveList)) {
            mapper.insertBatch(saveList);
        }
        TableBatchProcessResponse response = new TableBatchProcessResponse();
        response.setTotalCount(request.getSelectIds().size());
        response.setSuccessCount(saveList.size());
        return response;
    }

    @Override
    public void add(GlobalUserRoleRelationUpdateRequest request) {
        checkGlobalSystemUserRoleLegality(
                Collections.singletonList(request.getRoleId()));
        //检查用户的合法性
        checkUserLegality(request.getUserIds());
        List<UserRoleRelation> userRoleRelations = new ArrayList<>();
        request.getUserIds().forEach(userId -> {
            UserRoleRelation userRoleRelation = new UserRoleRelation();
            BeanUtils.copyProperties(request, userRoleRelation);
            userRoleRelation.setUserId(userId);
            userRoleRelation.setSourceId(UserRoleScope.SYSTEM);
            checkExist(userRoleRelation);
            userRoleRelation.setOrganizationId(UserRoleScope.SYSTEM);
            userRoleRelations.add(userRoleRelation);
        });
        mapper.insertBatch(userRoleRelations);
    }

    private List<UserRoleRelation> selectByUserIdAndRuleId(List<String> userIds, List<String> roleIds) {
        return queryChain().where(USER_ROLE_RELATION.USER_ID.in(userIds).and(USER_ROLE_RELATION.ROLE_ID.in(roleIds))).list();
    }

    private void checkUserLegality(@Valid List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            throw new CustomException(Translator.get("user.not.exist"));
        }
        if (userMapper.selectListByIds(userIds).size() != userIds.size()) {
            throw new CustomException(Translator.get("user.id.not.exist"));
        }
    }

    private void checkGlobalSystemUserRoleLegality(List<String> checkIdList) {
        List<UserRole> userRoleList = globalUserRoleService.getList(checkIdList);
        if (userRoleList.size() != checkIdList.size()) {
            throw new CustomException(Translator.get("user_role_not_exist"));
        }
        userRoleList.forEach(userRole -> {
            globalUserRoleService.checkSystemUserGroup(userRole);
            globalUserRoleService.checkGlobalUserRole(userRole);
        });
    }
}
