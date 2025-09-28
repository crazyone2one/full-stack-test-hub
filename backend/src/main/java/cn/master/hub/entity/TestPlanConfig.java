package cn.master.hub.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.math.BigDecimal;

import java.io.Serial;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测试计划配置 实体类。
 *
 * @author 11's papa
 * @since 2025-09-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "测试计划配置")
@Table("test_plan_config")
public class TestPlanConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    @Schema(description = "ID")
    private String id;

    /**
     * 测试计划ID
     */
    @Schema(description = "测试计划ID")
    private String testPlanId;

    /**
     * 是否自定更新功能用例状态
     */
    @Schema(description = "是否自定更新功能用例状态")
    private Boolean automaticStatusUpdate;

    /**
     * 是否允许重复添加用例
     */
    @Schema(description = "是否允许重复添加用例")
    private Boolean repeatCase;

    @Schema(description = "")
    private BigDecimal passThreshold;

    /**
     * 是否开启测试规划
     */
    @Schema(description = "是否开启测试规划")
    private Boolean testPlanning;

}
