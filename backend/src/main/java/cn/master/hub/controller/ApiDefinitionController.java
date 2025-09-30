package cn.master.hub.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import cn.master.hub.entity.ApiDefinition;
import cn.master.hub.service.ApiDefinitionService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 接口定义 控制层。
 *
 * @author 11's papa
 * @since 2025-09-29
 */
@RestController
@Tag(name = "接口定义接口")
@RequestMapping("/apiDefinition")
public class ApiDefinitionController {

    @Autowired
    private ApiDefinitionService apiDefinitionService;

    /**
     * 保存接口定义。
     *
     * @param apiDefinition 接口定义
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @Operation(description="保存接口定义")
    public boolean save(@RequestBody @Parameter(description="接口定义")ApiDefinition apiDefinition) {
        return apiDefinitionService.save(apiDefinition);
    }

    /**
     * 根据主键删除接口定义。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键删除接口定义")
    public boolean remove(@PathVariable @Parameter(description="接口定义主键") String id) {
        return apiDefinitionService.removeById(id);
    }

    /**
     * 根据主键更新接口定义。
     *
     * @param apiDefinition 接口定义
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新接口定义")
    public boolean update(@RequestBody @Parameter(description="接口定义主键") ApiDefinition apiDefinition) {
        return apiDefinitionService.updateById(apiDefinition);
    }

    /**
     * 查询所有接口定义。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有接口定义")
    public List<ApiDefinition> list() {
        return apiDefinitionService.list();
    }

    /**
     * 根据主键获取接口定义。
     *
     * @param id 接口定义主键
     * @return 接口定义详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取接口定义")
    public ApiDefinition getInfo(@PathVariable @Parameter(description="接口定义主键") String id) {
        return apiDefinitionService.getById(id);
    }

    /**
     * 分页查询接口定义。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询接口定义")
    public Page<ApiDefinition> page(@Parameter(description="分页信息") Page<ApiDefinition> page) {
        return apiDefinitionService.page(page);
    }

}
