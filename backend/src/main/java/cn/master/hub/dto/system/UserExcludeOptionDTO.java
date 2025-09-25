package cn.master.hub.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Created by 11's papa on 2025/9/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserExcludeOptionDTO extends ExcludeOptionDTO {
    @Schema(description =  "邮箱")
    private String email;
    private String name;
    private String id;
}
