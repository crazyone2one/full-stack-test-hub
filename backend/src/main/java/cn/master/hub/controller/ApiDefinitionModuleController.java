package cn.master.hub.controller;

import cn.master.hub.dto.BaseTreeNode;
import cn.master.hub.dto.api.ModuleUpdateRequest;
import cn.master.hub.dto.request.ApiModuleRequest;
import cn.master.hub.dto.request.ModuleCreateRequest;
import cn.master.hub.entity.ApiDefinitionModule;
import cn.master.hub.service.ApiDefinitionModuleService;
import cn.master.hub.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 接口模块 控制层。
 *
 * @author 11's papa
 * @since 2025-09-29
 */
@RestController
@Tag(name = "接口模块接口")
@RequestMapping("/api/definition/module")
public class ApiDefinitionModuleController {

    private final ApiDefinitionModuleService apiDefinitionModuleService;

    public ApiDefinitionModuleController(ApiDefinitionModuleService apiDefinitionModuleService) {
        this.apiDefinitionModuleService = apiDefinitionModuleService;
    }

    /**
     * 保存接口模块。
     *
     * @param request 接口模块
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @Operation(description = "接口测试-接口管理-模块-添加模块")
    public String save(@RequestBody @Validated ModuleCreateRequest request) {
        return apiDefinitionModuleService.add(request, SessionUtils.getCurrentUserName());
    }

    /**
     * 根据主键删除接口模块。
     *
     * @param id 主键
     */
    @DeleteMapping("remove/{id}")
    @Operation(description = "接口测试-接口管理-模块-删除模块")
    public void remove(@PathVariable @Parameter(description = "接口模块主键") String id) {
        apiDefinitionModuleService.deleteModule(id, SessionUtils.getCurrentUserName());
    }

    /**
     * 根据主键更新接口模块。
     *
     * @param request 接口模块
     */
    @PostMapping("update")
    @Operation(description = "接口测试-接口管理-模块-修改模块")
    public void update(@RequestBody @Parameter(description = "接口模块主键") ModuleUpdateRequest request) {
        apiDefinitionModuleService.update(request, SessionUtils.getCurrentUserName());
    }

    @PostMapping("tree")
    @Operation(description = "接口测试-接口管理-模块-查找模块")
    public List<BaseTreeNode> getTreeAndRequest(@RequestBody @Validated ApiModuleRequest request) {
        return apiDefinitionModuleService.getTree(request, false, true);
    }

    /**
     * 根据主键获取接口模块。
     *
     * @param id 接口模块主键
     * @return 接口模块详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取接口模块")
    public ApiDefinitionModule getInfo(@PathVariable @Parameter(description = "接口模块主键") String id) {
        return apiDefinitionModuleService.getById(id);
    }

    /**
     * 分页查询接口模块。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description = "分页查询接口模块")
    public Page<ApiDefinitionModule> page(@Parameter(description = "分页信息") Page<ApiDefinitionModule> page) {
        return apiDefinitionModuleService.page(page);
    }
    @PostMapping("/count")
    @Operation(summary = "接口测试-接口管理-模块-统计模块数量")
    public Map<String, Long> moduleCount(@Validated @RequestBody ApiModuleRequest request) {
        return apiDefinitionModuleService.moduleCount(request, false);
    }
}
