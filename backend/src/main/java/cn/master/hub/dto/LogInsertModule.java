package cn.master.hub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Created by 11's papa on 2025/9/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogInsertModule {
    @NotBlank
    private String operator;
    @NotBlank
    private String requestUrl;
    @NotBlank
    private String requestMethod;
}
