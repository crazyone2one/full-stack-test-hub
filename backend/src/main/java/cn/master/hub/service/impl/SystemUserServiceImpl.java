package cn.master.hub.service.impl;

import cn.master.hub.dto.UserCreateInfo;
import cn.master.hub.dto.request.BasePageRequest;
import cn.master.hub.dto.request.UserBatchCreateRequest;
import cn.master.hub.dto.response.UserBatchCreateResponse;
import cn.master.hub.entity.SystemUser;
import cn.master.hub.handler.Translator;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.handler.result.ResultCode;
import cn.master.hub.mapper.SystemUserMapper;
import cn.master.hub.service.OperationLogService;
import cn.master.hub.service.SystemUserService;
import cn.master.hub.service.log.UserLogService;
import cn.master.hub.util.JacksonUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static cn.master.hub.entity.table.SystemUserTableDef.SYSTEM_USER;

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

    @Override
    public Page<SystemUser> getUserPage(BasePageRequest request) {
        return queryChain()
                .where(SYSTEM_USER.NAME.like(request.getKeyword())
                        .or(SYSTEM_USER.EMAIL.like(request.getKeyword()))
                        .or(SYSTEM_USER.PHONE.like(request.getKeyword())))
                .page(new Page<>(request.getPage(), request.getPageSize()));
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
        return "";
    }

    private List<UserCreateInfo> saveUserAndRole(UserBatchCreateRequest userCreateDTO, String source, String operator, String requestPath) {
        userCreateDTO.getUserInfoList().forEach(userInfo -> {
            SystemUser user = new SystemUser();
            user.setName(userInfo.getName());
            user.setEmail(userInfo.getEmail());
            user.setPassword(passwordEncoder.encode(userInfo.getEmail()));
            user.setPhone(userInfo.getPhone());
            user.setSource(source);
            mapper.insert(user);
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
