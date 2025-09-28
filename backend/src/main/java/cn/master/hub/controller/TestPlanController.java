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
import cn.master.hub.entity.TestPlan;
import cn.master.hub.service.BaseTestPlanService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 测试计划 控制层。
 *
 * @author 11's papa
 * @since 2025-09-28
 */
@RestController
@Tag(name = "测试计划接口")
@RequestMapping("/testPlan")
public class TestPlanController {

    @Autowired
    private BaseTestPlanService baseTestPlanService;

    /**
     * 保存测试计划。
     *
     * @param testPlan 测试计划
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @Operation(description="保存测试计划")
    public boolean save(@RequestBody @Parameter(description="测试计划")TestPlan testPlan) {
        return baseTestPlanService.save(testPlan);
    }

    /**
     * 根据主键删除测试计划。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键删除测试计划")
    public boolean remove(@PathVariable @Parameter(description="测试计划主键") String id) {
        return baseTestPlanService.removeById(id);
    }

    /**
     * 根据主键更新测试计划。
     *
     * @param testPlan 测试计划
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新测试计划")
    public boolean update(@RequestBody @Parameter(description="测试计划主键") TestPlan testPlan) {
        return baseTestPlanService.updateById(testPlan);
    }

    /**
     * 查询所有测试计划。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有测试计划")
    public List<TestPlan> list() {
        return baseTestPlanService.list();
    }

    /**
     * 根据主键获取测试计划。
     *
     * @param id 测试计划主键
     * @return 测试计划详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取测试计划")
    public TestPlan getInfo(@PathVariable @Parameter(description="测试计划主键") String id) {
        return baseTestPlanService.getById(id);
    }

    /**
     * 分页查询测试计划。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询测试计划")
    public Page<TestPlan> page(@Parameter(description="分页信息") Page<TestPlan> page) {
        return baseTestPlanService.page(page);
    }

}
