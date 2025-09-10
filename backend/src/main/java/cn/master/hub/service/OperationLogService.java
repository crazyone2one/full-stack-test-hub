package cn.master.hub.service;

import cn.master.hub.entity.OperationLog;
import cn.master.hub.handler.log.LogDTO;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 操作日志 服务层。
 *
 * @author 11's papa
 * @since 2025-09-04
 */
public interface OperationLogService extends IService<OperationLog> {
    void add(OperationLog log);

    void batchAdd(List<LogDTO> logs);
}
