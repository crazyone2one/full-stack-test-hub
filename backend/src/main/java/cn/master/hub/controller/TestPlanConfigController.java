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
import cn.master.hub.entity.TestPlanConfig;
import cn.master.hub.service.TestPlanConfigService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 测试计划配置 控制层。
 *
 * @author 11's papa
 * @since 2025-09-28
 */
@RestController
@Tag(name = "测试计划配置接口")
@RequestMapping("/testPlanConfig")
public class TestPlanConfigController {

    @Autowired
    private TestPlanConfigService testPlanConfigService;

    /**
     * 保存测试计划配置。
     *
     * @param testPlanConfig 测试计划配置
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @Operation(description="保存测试计划配置")
    public boolean save(@RequestBody @Parameter(description="测试计划配置")TestPlanConfig testPlanConfig) {
        return testPlanConfigService.save(testPlanConfig);
    }

    /**
     * 根据主键删除测试计划配置。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键删除测试计划配置")
    public boolean remove(@PathVariable @Parameter(description="测试计划配置主键") String id) {
        return testPlanConfigService.removeById(id);
    }

    /**
     * 根据主键更新测试计划配置。
     *
     * @param testPlanConfig 测试计划配置
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新测试计划配置")
    public boolean update(@RequestBody @Parameter(description="测试计划配置主键") TestPlanConfig testPlanConfig) {
        return testPlanConfigService.updateById(testPlanConfig);
    }

    /**
     * 查询所有测试计划配置。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有测试计划配置")
    public List<TestPlanConfig> list() {
        return testPlanConfigService.list();
    }

    /**
     * 根据主键获取测试计划配置。
     *
     * @param id 测试计划配置主键
     * @return 测试计划配置详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取测试计划配置")
    public TestPlanConfig getInfo(@PathVariable @Parameter(description="测试计划配置主键") String id) {
        return testPlanConfigService.getById(id);
    }

    /**
     * 分页查询测试计划配置。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询测试计划配置")
    public Page<TestPlanConfig> page(@Parameter(description="分页信息") Page<TestPlanConfig> page) {
        return testPlanConfigService.page(page);
    }

}
