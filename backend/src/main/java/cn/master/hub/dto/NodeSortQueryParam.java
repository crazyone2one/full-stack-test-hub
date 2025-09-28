package cn.master.hub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Created by 11's papa on 2025/9/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeSortQueryParam {
    private String parentId;
    private String operator;
    private long pos;
}
