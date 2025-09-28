package cn.master.hub.controller;

import cn.master.hub.constants.HttpMethodConstants;
import cn.master.hub.constants.TestPlanResourceConfig;
import cn.master.hub.dto.BaseTreeNode;
import cn.master.hub.dto.request.TestPlanModuleCreateRequest;
import cn.master.hub.dto.request.TestPlanModuleUpdateRequest;
import cn.master.hub.entity.TestPlanModule;
import cn.master.hub.service.TestPlanManagementService;
import cn.master.hub.service.TestPlanModuleService;
import cn.master.hub.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * 测试计划模块 控制层。
 *
 * @author 11's papa
 * @since 2025-09-28
 */
@RestController
@Tag(name = "测试计划模块接口")
@RequiredArgsConstructor
@RequestMapping("/test-plan/module")
public class TestPlanModuleController {
    private final TestPlanModuleService testPlanModuleService;
    private final TestPlanManagementService testPlanManagementService;

    @PostMapping("save")
    @Operation(description = "测试计划管理-模块树-添加模块")
    public String save(@RequestBody @Parameter(description = "测试计划模块") @Validated TestPlanModuleCreateRequest request) {
        testPlanManagementService.checkModuleIsOpen(request.getProjectId(), TestPlanResourceConfig.CHECK_TYPE_PROJECT, Collections.singletonList(TestPlanResourceConfig.CHECK_TYPE_TEST_PLAN));
        return testPlanModuleService.add(request, SessionUtils.getCurrentUserName(), "/test-plan/module/add", HttpMethodConstants.POST.name());
    }

    /**
     * 根据主键删除测试计划模块。
     *
     * @param deleteId 主键
     */
    @GetMapping("remove/{deleteId}")
    @Operation(description = "测试计划管理-模块树-删除模块")
    public void remove(@PathVariable @Parameter(description = "测试计划模块主键") String deleteId) {
        testPlanManagementService.checkModuleIsOpen(deleteId, TestPlanResourceConfig.CHECK_TYPE_TEST_PLAN_MODULE, Collections.singletonList(TestPlanResourceConfig.CHECK_TYPE_TEST_PLAN));
        testPlanModuleService.deleteModule(deleteId, SessionUtils.getCurrentUserName(), "/test-plan/module/delete", HttpMethodConstants.GET.name());
    }

    /**
     * 根据主键更新测试计划模块。
     *
     * @param request 测试计划模块
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PostMapping("update")
    @Operation(description = "测试计划管理-模块树-修改模块")
    public boolean update(@RequestBody @Validated @Parameter(description = "测试计划模块主键") TestPlanModuleUpdateRequest request) {
        testPlanManagementService.checkModuleIsOpen(request.getId(), TestPlanResourceConfig.CHECK_TYPE_TEST_PLAN_MODULE, Collections.singletonList(TestPlanResourceConfig.CHECK_TYPE_TEST_PLAN));
        testPlanModuleService.update(request, SessionUtils.getCurrentUserName(), "/test-plan/module/update", HttpMethodConstants.POST.name());
        return true;
    }

    @GetMapping("/tree/{projectId}")
    @Operation(summary = "测试计划管理-模块树-查找模块")
    public List<BaseTreeNode> getTree(@PathVariable String projectId) {
        testPlanManagementService.checkModuleIsOpen(projectId, TestPlanResourceConfig.CHECK_TYPE_PROJECT, Collections.singletonList(TestPlanResourceConfig.CHECK_TYPE_TEST_PLAN));
        return testPlanModuleService.getTree(projectId);
    }

    /**
     * 查询所有测试计划模块。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description = "查询所有测试计划模块")
    public List<TestPlanModule> list() {
        return testPlanModuleService.list();
    }

    /**
     * 根据主键获取测试计划模块。
     *
     * @param id 测试计划模块主键
     * @return 测试计划模块详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取测试计划模块")
    public TestPlanModule getInfo(@PathVariable @Parameter(description = "测试计划模块主键") String id) {
        return testPlanModuleService.getById(id);
    }

    /**
     * 分页查询测试计划模块。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description = "分页查询测试计划模块")
    public Page<TestPlanModule> page(@Parameter(description = "分页信息") Page<TestPlanModule> page) {
        return testPlanModuleService.page(page);
    }

}
