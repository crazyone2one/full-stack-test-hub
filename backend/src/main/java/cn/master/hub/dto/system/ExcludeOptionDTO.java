package cn.master.hub.dto.system;

import cn.master.hub.dto.OptionDTO;
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
public class ExcludeOptionDTO extends OptionDTO {
    @Schema(description =  "是否已经关联过")
    private Boolean exclude = false;
}
