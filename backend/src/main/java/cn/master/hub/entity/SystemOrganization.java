package cn.master.hub.entity;

import cn.master.hub.handler.validation.Created;
import cn.master.hub.handler.validation.Updated;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 组织 实体类。
 *
 * @author the2n
 * @since 2025-09-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "组织")
@Table("system_organization")
public class SystemOrganization implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 组织ID
     */
    @Id
    @Schema(description = "组织ID")
    private String id;

    /**
     * 组织编号
     */
    @Schema(description = "组织编号")
    private Long num;

    @Schema(description = "组织名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{organization.name.not_blank}", groups = {Created.class, Updated.class})
    @Size(min = 1, max = 255, message = "{organization.name.length_range}", groups = {Created.class, Updated.class})
    private String name;

    @Schema(description =  "描述")
    @Size(max = 1000, groups = {Created.class, Updated.class})
    private String description;

    /**
     * 创建时间
     */
    @Column(onInsertValue = "now()")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createUser;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private String updateUser;

    /**
     * 是否删除
     */
    @Schema(description = "是否删除")
    private Boolean deleted;

    /**
     * 删除人
     */
    @Schema(description = "删除人")
    private String deleteUser;

    /**
     * 删除时间
     */
    @Schema(description = "删除时间")
    private LocalDateTime deleteTime;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    private Boolean enable;

}
