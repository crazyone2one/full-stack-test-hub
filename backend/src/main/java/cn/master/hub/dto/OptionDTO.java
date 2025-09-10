package cn.master.hub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Created by 11's papa on 2025/9/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionDTO implements Serializable {
    @Schema(description =  "选项ID")
    private String value;
    @Schema(description =  "选项名称")
    private String label;
}
