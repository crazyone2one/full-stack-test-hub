package cn.master.hub.service;

import cn.master.hub.dto.project.NodeSortUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Created by 11's papa on 2025/9/28
 */
@Service
@Transactional(rollbackFor = Exception.class)
public abstract class TestPlanSortService extends MoveNodeService {
    protected static final long DEFAULT_NODE_INTERVAL_POS = NodeSortUtils.DEFAULT_NODE_INTERVAL_POS;

    public abstract void updatePos(String id, long pos);

    public abstract void refreshPos(String testPlanId);

    private static final String MOVE_POS_OPERATOR_LESS = "lessThan";
    private static final String MOVE_POS_OPERATOR_MORE = "moreThan";
    private static final String DRAG_NODE_NOT_EXIST = "drag_node.not.exist";
}
