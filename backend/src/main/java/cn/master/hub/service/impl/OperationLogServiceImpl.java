package cn.master.hub.service.impl;

import cn.master.hub.entity.OperationHistory;
import cn.master.hub.entity.OperationLog;
import cn.master.hub.mapper.OperationHistoryMapper;
import cn.master.hub.mapper.OperationLogMapper;
import cn.master.hub.service.OperationLogService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 操作日志 服务层实现。
 *
 * @author 11's papa
 * @since 2025-09-04
 */
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
    private final OperationHistoryMapper operationHistoryMapper;

    @Override
    public void batchAdd(List<OperationLog> logs) {
        if (CollectionUtils.isEmpty(logs)) {
            return;
        }
        logs.forEach(log -> {
            log.setContent(subStrContent(log.getContent()));
            mapper.insert(log);
            if (log.getHistory()) {
                operationHistoryMapper.insert(getHistory(log));
            }
        });
    }

    private OperationHistory getHistory(OperationLog log) {
        OperationHistory history = new OperationHistory();
        BeanUtils.copyProperties(log, history);
        return history;
    }

    private String subStrContent(String content) {
        if (StringUtils.isNotBlank(content) && content.length() > 500) {
            return content.substring(0, 499);
        }
        return content;
    }
}
