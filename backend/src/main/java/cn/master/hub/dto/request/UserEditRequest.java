package cn.master.hub.dto.request;

import cn.master.hub.dto.UserCreateInfo;
import cn.master.hub.handler.validation.Created;
import cn.master.hub.handler.validation.Updated;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/12
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserEditRequest extends UserCreateInfo {
    @Schema(description =  "用户组", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(groups = {Created.class, Updated.class}, message = "{user_role.id.not_blank}")
    List<@Valid @NotBlank(message = "{user_role.id.not_blank}", groups = {Created.class, Updated.class}) String> userRoleIdList;
}
