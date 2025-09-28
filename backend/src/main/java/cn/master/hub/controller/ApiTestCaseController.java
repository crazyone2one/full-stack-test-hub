package cn.master.hub.controller;

import cn.master.hub.dto.api.ApiTestCaseAddRequest;
import cn.master.hub.entity.ApiTestCase;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.handler.log.annotation.Log;
import cn.master.hub.service.ApiTestCaseService;
import cn.master.hub.service.log.ApiTestCaseLogService;
import cn.master.hub.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 接口用例 控制层。
 *
 * @author 11's papa
 * @since 2025-09-26
 */
@RestController
@Tag(name = "接口用例接口")
@RequestMapping("/api/case")
public class ApiTestCaseController {
    private final ApiTestCaseService apiTestCaseService;

    public ApiTestCaseController(ApiTestCaseService apiTestCaseService) {
        this.apiTestCaseService = apiTestCaseService;
    }

    /**
     * 保存接口用例。
     *
     * @param request 接口用例
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @Operation(description = "保存接口用例")
    @Log(type = OperationLogType.ADD, expression = "#msClass.addLog(#request)", msClass = ApiTestCaseLogService.class)
    public ApiTestCase save(@RequestBody @Parameter(description = "接口用例") ApiTestCaseAddRequest request) {
        return apiTestCaseService.addCase(request, SessionUtils.getCurrentUserName());
    }

    /**
     * 根据主键删除接口用例。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description = "根据主键删除接口用例")
    public boolean remove(@PathVariable @Parameter(description = "接口用例主键") String id) {
        return apiTestCaseService.removeById(id);
    }

    /**
     * 根据主键更新接口用例。
     *
     * @param apiTestCase 接口用例
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description = "根据主键更新接口用例")
    public boolean update(@RequestBody @Parameter(description = "接口用例主键") ApiTestCase apiTestCase) {
        return apiTestCaseService.updateById(apiTestCase);
    }

    /**
     * 查询所有接口用例。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description = "查询所有接口用例")
    public List<ApiTestCase> list() {
        return apiTestCaseService.list();
    }

    /**
     * 根据主键获取接口用例。
     *
     * @param id 接口用例主键
     * @return 接口用例详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取接口用例")
    public ApiTestCase getInfo(@PathVariable @Parameter(description = "接口用例主键") String id) {
        return apiTestCaseService.getById(id);
    }

    /**
     * 分页查询接口用例。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description = "分页查询接口用例")
    public Page<ApiTestCase> page(@Parameter(description = "分页信息") Page<ApiTestCase> page) {
        return apiTestCaseService.page(page);
    }

}
