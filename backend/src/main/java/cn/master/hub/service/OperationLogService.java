package cn.master.hub.service;

import com.mybatisflex.core.service.IService;
import cn.master.hub.entity.OperationLog;

import java.util.List;

/**
 * 操作日志 服务层。
 *
 * @author 11's papa
 * @since 2025-09-04
 */
public interface OperationLogService extends IService<OperationLog> {
    void batchAdd(List<OperationLog> logs);
}
