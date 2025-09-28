package cn.master.hub.service;

import cn.master.hub.dto.api.ApiTestCaseAddRequest;
import com.mybatisflex.core.service.IService;
import cn.master.hub.entity.ApiTestCase;

/**
 * 接口用例 服务层。
 *
 * @author 11's papa
 * @since 2025-09-26
 */
public interface ApiTestCaseService extends IService<ApiTestCase> {

    ApiTestCase addCase(ApiTestCaseAddRequest request, String userId);
}
