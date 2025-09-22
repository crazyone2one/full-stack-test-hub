package cn.master.hub.service;

import cn.master.hub.dto.system.TableBatchProcessDTO;
import cn.master.hub.entity.SystemUser;
import com.mybatisflex.core.query.QueryChain;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static cn.master.hub.entity.table.SystemUserTableDef.SYSTEM_USER;

/**
 * @author Created by 11's papa on 2025/9/22
 */
@Service
public class UserToolService {
    public List<String> getBatchUserIds(TableBatchProcessDTO request) {
        if (request.isSelectAll()) {
            List<SystemUser> userList = QueryChain.of(SystemUser.class)
                    .where(SYSTEM_USER.NAME.like(request.getCondition().getKeyword())
                            .or(SYSTEM_USER.EMAIL.like(request.getCondition().getKeyword()))
                            .or(SYSTEM_USER.PHONE.like(request.getCondition().getKeyword())))
                    .list();
            List<String> userIdList = userList.stream().map(SystemUser::getId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(request.getExcludeIds())) {
                userIdList.removeAll(request.getExcludeIds());
            }
            return userIdList;
        } else {
            return request.getSelectIds();
        }
    }
}
