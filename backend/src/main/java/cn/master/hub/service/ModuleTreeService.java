package cn.master.hub.service;

import cn.master.hub.constants.ModuleConstants;
import cn.master.hub.dto.*;
import cn.master.hub.dto.project.NodeSortUtils;
import cn.master.hub.dto.request.NodeMoveRequest;
import cn.master.hub.handler.Translator;
import cn.master.hub.handler.exception.CustomException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Created by 11's papa on 2025/9/28
 */
@Service
public class ModuleTreeService {
    public static final long LIMIT_POS = NodeSortUtils.DEFAULT_NODE_INTERVAL_POS;
    private static final String MOVE_POS_OPERATOR_LESS = "lessThan";
    private static final String MOVE_POS_OPERATOR_MORE = "moreThan";
    private static final String MOVE_POS_OPERATOR_LATEST = "latest";
    private static final String DRAG_NODE_NOT_EXIST = "drag_node.not.exist";

    public BaseTreeNode getDefaultModule(String name) {
        //默认模块下不允许创建子模块。  它本身也就是叶子节点。
        return new BaseTreeNode(ModuleConstants.DEFAULT_NODE_ID, name, ModuleConstants.NODE_TYPE_DEFAULT, ModuleConstants.ROOT_NODE_PARENT_ID);
    }

    public List<BaseTreeNode> buildTreeAndCountResource(List<BaseTreeNode> traverseList, boolean haveVirtualRootNode, String virtualRootName) {
        List<BaseTreeNode> baseTreeNodeList = new ArrayList<>();
        if (haveVirtualRootNode) {
            BaseTreeNode defaultNode = this.getDefaultModule(virtualRootName);
            defaultNode.genModulePath(null);
            baseTreeNodeList.add(defaultNode);
        }
        int lastSize = 0;
        Map<String, BaseTreeNode> baseTreeNodeMap = new HashMap<>();
        // 根节点预先处理
        baseTreeNodeList.addAll(traverseList.stream().filter(treeNode -> Strings.CS.equals(treeNode.getParentId(), ModuleConstants.ROOT_NODE_PARENT_ID)).toList());
        baseTreeNodeList.forEach(item -> baseTreeNodeMap.put(item.getId(), item));
        traverseList = (List<BaseTreeNode>) CollectionUtils.removeAll(traverseList, baseTreeNodeList);
        // 循环处理子节点
        while (CollectionUtils.isNotEmpty(traverseList) && traverseList.size() != lastSize) {
            lastSize = traverseList.size();
            List<BaseTreeNode> notMatchedList = new ArrayList<>();
            for (BaseTreeNode treeNode : traverseList) {
                if (!baseTreeNodeMap.containsKey(treeNode.getParentId()) && !Strings.CS.equals(treeNode.getParentId(), ModuleConstants.ROOT_NODE_PARENT_ID)) {
                    notMatchedList.add(treeNode);
                    continue;
                }
                BaseTreeNode node = new BaseTreeNode(treeNode.getId(), treeNode.getName(), treeNode.getType(), treeNode.getParentId());
                node.genModulePath(baseTreeNodeMap.get(treeNode.getParentId()));
                baseTreeNodeMap.put(treeNode.getId(), node);

                if (Strings.CS.equals(treeNode.getParentId(), ModuleConstants.ROOT_NODE_PARENT_ID)) {
                    baseTreeNodeList.add(node);
                } else if (baseTreeNodeMap.containsKey(treeNode.getParentId())) {
                    baseTreeNodeMap.get(treeNode.getParentId()).addChild(node);
                }
            }
            traverseList = notMatchedList;
        }
        return baseTreeNodeList;
    }

    public NodeSortDTO getNodeSortDTO(NodeMoveRequest request, Function<String, BaseModule> selectIdNodeFunc, Function<NodeSortQueryParam, BaseModule> selectPosNodeFunc) {
        if (Strings.CS.equals(request.getDragNodeId(), request.getDropNodeId())) {
            //两种节点不能一样
            throw new CustomException(Translator.get("invalid_parameter") + ": drag node  and drop node");
        }
        BaseModule dragNode = selectIdNodeFunc.apply(request.getDragNodeId());
        if (dragNode == null) {
            throw new CustomException(Translator.get(DRAG_NODE_NOT_EXIST) + ":" + request.getDragNodeId());
        }
        BaseModule dropNode = selectIdNodeFunc.apply(request.getDropNodeId());
        if (dropNode == null) {
            throw new CustomException(Translator.get(DRAG_NODE_NOT_EXIST) + ":" + request.getDropNodeId());

        }
        BaseModule parentModule;
        BaseModule previousNode;
        BaseModule nextNode = null;
        if (request.getDropPosition() == 0) {
            //dropPosition=0: 放到dropNode节点内，最后一个节点之后
            parentModule = new BaseModule(dropNode.getId(), dropNode.getName(), dropNode.getPos(), dropNode.getProjectId(), dropNode.getParentId());

            NodeSortQueryParam sortParam = new NodeSortQueryParam();
            sortParam.setParentId(dropNode.getId());
            sortParam.setOperator(MOVE_POS_OPERATOR_LATEST);
            previousNode = selectPosNodeFunc.apply(sortParam);
        } else {
            if (Strings.CS.equals(dropNode.getParentId(), ModuleConstants.ROOT_NODE_PARENT_ID)) {
                parentModule = new BaseModule(ModuleConstants.ROOT_NODE_PARENT_ID, ModuleConstants.ROOT_NODE_PARENT_ID, 0, dragNode.getProjectId(), ModuleConstants.ROOT_NODE_PARENT_ID);
            } else {
                parentModule = selectIdNodeFunc.apply(dropNode.getParentId());
            }
            if (request.getDropPosition() == 1) {
                //dropPosition=1: 放到dropNode节点后，原dropNode后面的节点之前
                previousNode = dropNode;

                NodeSortQueryParam sortParam = new NodeSortQueryParam();
                sortParam.setParentId(parentModule.getId());
                sortParam.setPos(previousNode.getPos());
                sortParam.setOperator(MOVE_POS_OPERATOR_MORE);
                nextNode = selectPosNodeFunc.apply(sortParam);
            } else if (request.getDropPosition() == -1) {
                //dropPosition=-1: 放到dropNode节点前，原dropNode前面的节点之后
                nextNode = dropNode;

                NodeSortQueryParam sortParam = new NodeSortQueryParam();
                sortParam.setParentId(parentModule.getId());
                sortParam.setPos(nextNode.getPos());
                sortParam.setOperator(MOVE_POS_OPERATOR_LESS);
                previousNode = selectPosNodeFunc.apply(sortParam);
            } else {
                throw new CustomException(Translator.get("invalid_parameter") + ": dropPosition");
            }
        }

        return new NodeSortDTO(dragNode, parentModule, previousNode, nextNode);
    }

    public ModuleSortCountResultDTO sort(NodeSortDTO nodeMoveDTO) {
        // 获取相邻节点
        BaseModule previousNode = nodeMoveDTO.getPreviousNode();
        BaseModule nextNode = nodeMoveDTO.getNextNode();

        return NodeSortUtils.countModuleSort(
                previousNode == null ? -1 : previousNode.getPos(),
                nextNode == null ? -1 : nextNode.getPos());
    }
}
