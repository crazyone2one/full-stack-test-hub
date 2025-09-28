package cn.master.hub.dto.project;

import cn.master.hub.dto.ModuleSortCountResultDTO;

/**
 * @author Created by 11's papa on 2025/9/26
 */
public class NodeSortUtils {
    //默认节点间隔
    public static final long DEFAULT_NODE_INTERVAL_POS = 4096;

    public static ModuleSortCountResultDTO countModuleSort(long previousNodePos, long nextNodePos) {
        boolean refreshPos = false;
        long pos;
        if (nextNodePos < 0 && previousNodePos < 0) {
            pos = 0;
        } else if (nextNodePos < 0) {
            pos = previousNodePos + DEFAULT_NODE_INTERVAL_POS;
        } else if (previousNodePos < 0) {
            pos = nextNodePos / 2;
            if (pos < 2) {
                refreshPos = true;
            }
        } else {
            long quantityDifference = (nextNodePos - previousNodePos) / 2;
            if (quantityDifference <= 2) {
                refreshPos = true;
            }
            pos = previousNodePos + quantityDifference;
        }
        return new ModuleSortCountResultDTO(refreshPos, pos);
    }
}
