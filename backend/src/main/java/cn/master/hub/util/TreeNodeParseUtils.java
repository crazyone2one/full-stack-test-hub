package cn.master.hub.util;

import cn.master.hub.dto.BaseTreeNode;
import cn.master.hub.entity.SystemOrganization;
import cn.master.hub.entity.SystemProject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Created by 11's papa on 2025/9/23
 */
public class TreeNodeParseUtils {
    public static List<BaseTreeNode> parseOrgProjectMap(Map<SystemOrganization, List<SystemProject>> orgProjectMap) {
        List<BaseTreeNode> returnList = new ArrayList<>();
        for (Map.Entry<SystemOrganization, List<SystemProject>> entry : orgProjectMap.entrySet()) {
            SystemOrganization organization = entry.getKey();
            List<SystemProject> projects = entry.getValue();

            BaseTreeNode orgNode = new BaseTreeNode(organization.getId(), organization.getName(), SystemOrganization.class.getName());
            returnList.add(orgNode);

            for (SystemProject project : projects) {
                BaseTreeNode projectNode = new BaseTreeNode(project.getId(), project.getName(), SystemProject.class.getName());
                orgNode.addChild(projectNode);
            }
        }
        return returnList;
    }
}
