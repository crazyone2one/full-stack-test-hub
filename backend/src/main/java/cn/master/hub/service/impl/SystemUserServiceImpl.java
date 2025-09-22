package cn.master.hub.service.impl;

import cn.master.hub.constants.UserRoleType;
import cn.master.hub.dto.UserCreateInfo;
import cn.master.hub.dto.request.BasePageRequest;
import cn.master.hub.dto.request.UserBatchCreateRequest;
import cn.master.hub.dto.request.UserEditRequest;
import cn.master.hub.dto.response.UserBatchCreateResponse;
import cn.master.hub.dto.response.UserTableResponse;
import cn.master.hub.dto.system.TableBatchProcessDTO;
import cn.master.hub.dto.system.TableBatchProcessResponse;
import cn.master.hub.dto.system.UserSelectOption;
import cn.master.hub.dto.system.request.UserChangeEnableRequest;
import cn.master.hub.entity.SystemProject;
import cn.master.hub.entity.SystemUser;
import cn.master.hub.entity.UserRole;
import cn.master.hub.entity.UserRoleRelation;
import cn.master.hub.handler.Translator;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.handler.result.ResultCode;
import cn.master.hub.mapper.SystemUserMapper;
import cn.master.hub.service.*;
import cn.master.hub.service.log.UserLogService;
import cn.master.hub.util.JacksonUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static cn.master.hub.constants.InternalUserRole.MEMBER;
import static cn.master.hub.entity.table.SystemProjectTableDef.SYSTEM_PROJECT;
import static cn.master.hub.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.hub.entity.table.UserRoleTableDef.USER_ROLE;

/**
 * 用户 服务层实现。
 *
 * @author the2n
 * @since 2025-08-29
 */
@Service
@RequiredArgsConstructor
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {
    private final OperationLogService operationLogService;
    private final UserLogService userLogService;
    private final PasswordEncoder passwordEncoder;
    private final GlobalUserRoleService globalUserRoleService;
    private final BaseUserRoleRelationService userRoleRelationService;
    private final UserToolService userToolService;

    @Override
    public Page<UserTableResponse> getUserPage(BasePageRequest request) {
        Page<UserTableResponse> page = queryChain()
                .where(SYSTEM_USER.NAME.like(request.getKeyword())
                        .or(SYSTEM_USER.EMAIL.like(request.getKeyword()))
                        .or(SYSTEM_USER.PHONE.like(request.getKeyword())))
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), UserTableResponse.class);
        List<UserTableResponse> userList = page.getRecords();
        if (!userList.isEmpty()) {
            List<String> userIdList = userList.stream().map(SystemUser::getId).collect(Collectors.toList());
            Map<String, UserTableResponse> roleAndOrganizationMap = userRoleRelationService.selectGlobalUserRoleAndOrganization(userIdList);
            for (UserTableResponse user : userList) {
                UserTableResponse roleOrgModel = roleAndOrganizationMap.get(user.getId());
                if (roleOrgModel != null) {
                    user.setUserRoleList(roleOrgModel.getUserRoleList());
                    user.setOrganizationList(roleOrgModel.getOrganizationList());
                }
            }
        }
        return page;
    }

    @Override
    public UserBatchCreateResponse addUser(UserBatchCreateRequest userCreateDTO, String source, String operator) {
        UserBatchCreateResponse response = new UserBatchCreateResponse();
        //检查用户邮箱的合法性
        Map<String, String> errorEmails = validateUserInfo(userCreateDTO.getUserInfoList().stream().map(UserCreateInfo::getEmail).toList());
        if (!errorEmails.isEmpty()) {
            response.setErrorEmails(errorEmails);
            throw new CustomException(ResultCode.INVITE_EMAIL_EXIST, JacksonUtils.toJSONString(errorEmails.keySet()));
        } else {
            response.setSuccessList(saveUserAndRole(userCreateDTO, source, operator, "/system/user/addUser"));
        }
        return response;
    }

    @Override
    public String addUser(UserCreateInfo userCreateDTO, String source, String currentUserName) {
        SystemUser user = new SystemUser();
        user.setName(userCreateDTO.getName());
        user.setEmail(userCreateDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userCreateDTO.getEmail()));
        user.setPhone(userCreateDTO.getPhone());
        user.setSource(source);
        user.setCreateUser(currentUserName);
        user.setUpdateUser(currentUserName);
        user.setEnable(true);
        mapper.insert(user);
//        userRoleRelationService.updateUserSystemGlobalRole(user, user.getUpdateUser(), userCreateDTO.getUserRoleIdList());
        return "";
    }

    @Override
    public void updateUser(SystemUser user) {
        if (StringUtils.isNotBlank(user.getEmail())) {
            boolean exists = queryChain().where(SYSTEM_USER.EMAIL.eq(user.getEmail()).and(SYSTEM_USER.ID.ne(user.getId()))).exists();
            if (exists) {
                throw new CustomException(Translator.get("user_email_already_exists"));
            }
        }
        SystemUser userFromDB = mapper.selectOneById(user.getId());
        if (user.getLastOrganizationId() != null && !Strings.CS.equals(user.getLastOrganizationId(), userFromDB.getLastOrganizationId())
                && !isSuperUser(user.getId())) {
            List<SystemProject> projects = getProjectListByWsAndUserId(user.getId(), user.getLastOrganizationId());
            if (!projects.isEmpty()) {
                // 如果传入的 last_project_id 是 last_organization_id 下面的
                boolean present = projects.stream().anyMatch(p -> Strings.CS.equals(p.getId(), user.getLastProjectId()));
                if (!present) {
                    user.setLastProjectId(projects.getFirst().getId());
                }
            } else {
                user.setLastProjectId(StringUtils.EMPTY);
            }
        }
    }

    private List<SystemProject> getProjectListByWsAndUserId(String id, String lastOrganizationId) {
        List<SystemProject> projects = QueryChain.of(SystemProject.class)
                .where(SYSTEM_PROJECT.ORGANIZATION_ID.eq(lastOrganizationId).and(SYSTEM_PROJECT.ENABLE.eq(true))).list();
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class).where(UserRoleRelation::getUserId).eq(id).list();
        List<SystemProject> projectList = new ArrayList<>();
        userRoleRelations.forEach(userRoleRelation -> projects.forEach(project -> {
            if (Strings.CS.equals(userRoleRelation.getSourceId(), project.getId())) {
                if (!projectList.contains(project)) {
                    projectList.add(project);
                }
            }
        }));
        return projectList;
    }

    @Override
    public boolean isSuperUser(String id) {
        return QueryChain.of(UserRoleRelation.class)
                .where(UserRoleRelation::getUserId).eq(id).and(UserRoleRelation::getRoleId).eq("admin")
                .exists();
    }

    @Override
    public List<UserSelectOption> getGlobalSystemRoleList() {
        List<UserSelectOption> returnList = new ArrayList<>();
        QueryChain.of(UserRole.class).where(USER_ROLE.SCOPE_ID.eq("global").and(USER_ROLE.TYPE.eq(UserRoleType.SYSTEM.name()))).list()
                .forEach(userRole -> {
                    UserSelectOption userRoleOption = new UserSelectOption();
                    userRoleOption.setId(userRole.getId());
                    userRoleOption.setName(userRole.getName());
                    userRoleOption.setSelected(Strings.CS.equals(userRole.getId(), MEMBER.getValue()));
                    userRoleOption.setCloseable(!Strings.CS.equals(userRole.getId(), MEMBER.getValue()));
                    returnList.add(userRoleOption);
                });
        return returnList;
    }

    @Override
    public UserEditRequest updateUser(UserEditRequest request, String operator) {
//        globalUserRoleService.checkRoleIsGlobalAndHaveMember(request.getUserRoleIdList(), true);
        checkUserEmail(request.getId(), request.getEmail());
        SystemUser user = new SystemUser();
        BeanUtils.copyProperties(request, user);
        user.setUpdateUser(operator);
        mapper.update(user);
        userRoleRelationService.updateUserSystemGlobalRole(user, user.getUpdateUser(), request.getUserRoleIdList());
        return request;
    }

    @Override
    public TableBatchProcessResponse deleteUser(TableBatchProcessDTO request, String operatorId, String operatorName) {
        List<String> userIdList = userToolService.getBatchUserIds(request);
        checkUserInDb(userIdList);
        checkProcessUserAndThrowException(userIdList, operatorId, operatorName, Translator.get("user.not.delete"));
        TableBatchProcessResponse response = new TableBatchProcessResponse();
        response.setTotalCount(userIdList.size());
        response.setSuccessCount(mapper.deleteBatchByIds(userIdList));
        //删除用户角色关系
        userRoleRelationService.deleteByUserIdList(userIdList);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TableBatchProcessResponse updateUserEnable(UserChangeEnableRequest request, String operatorId, String operatorName) {
        request.setSelectIds(userToolService.getBatchUserIds(request));
        checkUserInDb(request.getSelectIds());
        if (!request.isEnable()) {
            //不能禁用当前用户和admin
            this.checkProcessUserAndThrowException(request.getSelectIds(), operatorId, operatorName, Translator.get("user.not.disable"));
        }
        updateChain().set(SystemUser::getEnable, request.isEnable()).set(SystemUser::getUpdateUser, operatorName)
                .where(SystemUser::getId).in(request.getSelectIds()).update();
        TableBatchProcessResponse response = new TableBatchProcessResponse();
        response.setTotalCount(request.getSelectIds().size());
        response.setSuccessCount(request.getSelectIds().size());
        return response;
    }

    private void checkProcessUserAndThrowException(List<String> userIdList, String operatorId, String operatorName, String exceptionMessage) {
        for (String userId : userIdList) {
            //当前用户或admin不能被操作
            if (Strings.CS.equals(userId, operatorId)) {
                throw new CustomException(exceptionMessage + ":" + operatorName);
            } else if (Strings.CS.equals(userId, "admin")) {
                throw new CustomException(exceptionMessage + ": admin");
            }
        }
    }

    private void checkUserInDb(List<String> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            throw new CustomException(Translator.get("user.not.exist"));
        }
        List<SystemUser> userInDb = mapper.selectListByIds(userIdList);
        if (userIdList.size() != userInDb.size()) {
            throw new CustomException(Translator.get("user.not.exist"));
        }
    }

    public void checkUserEmail(String id, String email) {
        boolean exists = queryChain().where(SYSTEM_USER.EMAIL.eq(email).and(SYSTEM_USER.ID.ne(id))).exists();
        if (exists) {
            throw new CustomException(Translator.get("user_email_already_exists"));
        }
    }

    private List<UserCreateInfo> saveUserAndRole(UserBatchCreateRequest userCreateDTO, String source, String operator, String requestPath) {
        userCreateDTO.getUserInfoList().forEach(userInfo -> {
            SystemUser user = new SystemUser();
            user.setName(userInfo.getName());
            user.setEmail(userInfo.getEmail());
            user.setPassword(passwordEncoder.encode(userInfo.getEmail()));
            user.setPhone(userInfo.getPhone());
            user.setSource(source);
            user.setEnable(true);
            user.setCreateUser(operator);
            user.setUpdateUser(operator);
            mapper.insert(user);
            userRoleRelationService.updateUserSystemGlobalRole(user, user.getUpdateUser(), userCreateDTO.getUserRoleIdList());
        });
        operationLogService.batchAdd(userLogService.getBatchAddLogs(userCreateDTO.getUserInfoList(), operator, requestPath));
        return userCreateDTO.getUserInfoList();
    }

    private Map<String, String> validateUserInfo(Collection<String> createEmails) {
        Map<String, String> errorMessage = new HashMap<>();
        String userEmailRepeatError = Translator.get("user.email.repeat");
        //判断参数内是否含有重复邮箱
        List<String> emailList = new ArrayList<>();
        Map<String, String> userInDbMap = queryChain().where(SYSTEM_USER.EMAIL.in(createEmails)).list()
                .stream().collect(Collectors.toMap(SystemUser::getEmail, SystemUser::getId));
        for (String createEmail : createEmails) {
            if (emailList.contains(createEmail)) {
                errorMessage.put(createEmail, userEmailRepeatError);
            } else {
                //判断邮箱是否已存在数据库中
                if (userInDbMap.containsKey(createEmail)) {
                    errorMessage.put(createEmail, userEmailRepeatError);
                } else {
                    emailList.add(createEmail);
                }
            }
        }
        return errorMessage;
    }
}
