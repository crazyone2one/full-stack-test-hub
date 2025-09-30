package cn.master.hub.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 接口模块 实体类。
 *
 * @author 11's papa
 * @since 2025-09-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "接口模块")
@Table("api_definition_module")
public class ApiDefinitionModule implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 接口模块pk
     */
    @Id
    @Schema(description = "接口模块pk")
    private String id;

    /**
     * 模块名称
     */
    @Schema(description = "模块名称")
    private String name;

    /**
     * 父级fk
     */
    @Schema(description = "父级fk")
    private String parentId;

    /**
     * 项目fk
     */
    @Schema(description = "项目fk")
    private String projectId;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Long pos;

    /**
     * 创建时间
     */
    @Column(onInsertValue = "now()")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private String updateUser;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createUser;

    /**
     * 删除状态
     */
    @Schema(description = "删除状态")
    private Boolean deleted;

}
