package cn.master.hub.handler;

import cn.master.hub.dto.PermissionDefinitionItem;
import lombok.Data;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/9
 */
@Data
public class PermissionCache {
    private List<PermissionDefinitionItem> permissionDefinition;
}
