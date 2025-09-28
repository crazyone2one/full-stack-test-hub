package cn.master.hub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Created by 11's papa on 2025/9/28
 */
@Data
@AllArgsConstructor
public class ModuleSortCountResultDTO {
    private boolean isRefreshPos;
    private long pos;
}
