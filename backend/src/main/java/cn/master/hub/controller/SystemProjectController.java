package cn.master.hub.controller;

import cn.master.hub.dto.UserDTO;
import cn.master.hub.dto.request.ProjectRequest;
import cn.master.hub.dto.request.ProjectSwitchRequest;
import cn.master.hub.dto.response.ProjectDTO;
import cn.master.hub.entity.SystemProject;
import cn.master.hub.handler.validation.Created;
import cn.master.hub.handler.validation.Updated;
import cn.master.hub.service.SystemProjectService;
import cn.master.hub.util.SessionUtils;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 项目 控制层。
 *
 * @author 11's papa
 * @since 2025-09-01
 */
@RestController
@Tag(name = "项目接口")
@RequiredArgsConstructor
@RequestMapping("/system/project")
public class SystemProjectController {

    private final SystemProjectService systemProjectService;

    /**
     * 保存项目。
     *
     * @param systemProject 项目
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @Operation(description = "保存项目")
    public boolean save(@RequestBody @Validated({Created.class}) SystemProject systemProject) {
        return systemProjectService.save(systemProject);
    }

    /**
     * 根据主键删除项目。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description = "根据主键删除项目")
    public boolean remove(@PathVariable @Parameter(description = "项目主键") String id) {
        return systemProjectService.removeById(id);
    }

    /**
     * 根据主键更新项目。
     *
     * @param request 项目
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description = "根据主键更新项目")
    public ProjectDTO update(@RequestBody @Validated({Updated.class}) ProjectRequest request) {
        return systemProjectService.update(request, SessionUtils.getCurrentUserName());
    }

    /**
     * 查询所有项目。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description = "查询所有项目")
    public List<SystemProject> list() {
        return systemProjectService.list();
    }

    /**
     * 根据主键获取项目。
     *
     * @param id 项目主键
     * @return 项目详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取项目")
    public ProjectDTO getInfo(@PathVariable @Parameter(description = "项目主键") String id) {
        return systemProjectService.getProjectById(id);
    }

    /**
     * 分页查询项目。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description = "分页查询项目")
    public Page<SystemProject> page(@Parameter(description = "分页信息") Page<SystemProject> page) {
        return systemProjectService.page(page);
    }
    @PostMapping("/switch")
    @Operation(summary = "切换项目")
    //@RequiresPermissions(PermissionConstants.PROJECT_BASE_INFO_READ)
    //@CheckOwner(resourceId = "#request.projectId", resourceType = "project")
    public UserDTO switchProject(@RequestBody ProjectSwitchRequest request) {
        return systemProjectService.switchProject(request, Objects.requireNonNull(SessionUtils.getCurrentUser()).user().getId());
    }
}
