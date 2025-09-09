package cn.master.hub.controller;

import cn.master.hub.entity.SystemOrganization;
import cn.master.hub.service.SystemOrganizationService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 组织 控制层。
 *
 * @author the2n
 * @since 2025-09-01
 */
@RestController
@Tag(name = "组织接口")
@RequestMapping("/organization")
@RequiredArgsConstructor
public class SystemOrganizationController {

    private final SystemOrganizationService systemOrganizationService;

    /**
     * 保存组织。
     *
     * @param systemOrganization 组织
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @Operation(description="保存组织")
    public boolean save(@RequestBody @Parameter(description="组织")SystemOrganization systemOrganization) {
        return systemOrganizationService.save(systemOrganization);
    }

    /**
     * 根据主键删除组织。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键删除组织")
    public boolean remove(@PathVariable @Parameter(description="组织主键") String id) {
        return systemOrganizationService.removeById(id);
    }

    /**
     * 根据主键更新组织。
     *
     * @param systemOrganization 组织
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新组织")
    public boolean update(@RequestBody @Parameter(description="组织主键") SystemOrganization systemOrganization) {
        return systemOrganizationService.updateById(systemOrganization);
    }

    /**
     * 查询所有组织。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有组织")
    public List<SystemOrganization> list() {
        return systemOrganizationService.list();
    }

    /**
     * 根据主键获取组织。
     *
     * @param id 组织主键
     * @return 组织详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取组织")
    public SystemOrganization getInfo(@PathVariable @Parameter(description="组织主键") String id) {
        return systemOrganizationService.getById(id);
    }

    /**
     * 分页查询组织。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询组织")
    public Page<SystemOrganization> page(@Parameter(description="分页信息") Page<SystemOrganization> page) {
        return systemOrganizationService.page(page);
    }

}
