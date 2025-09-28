package cn.master.hub.service;

import cn.master.hub.constants.TestPlanResourceConfig;
import cn.master.hub.entity.SystemProject;
import cn.master.hub.entity.TestPlan;
import cn.master.hub.entity.TestPlanModule;
import cn.master.hub.handler.Translator;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.mapper.SystemProjectMapper;
import com.mybatisflex.core.query.QueryChain;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

import static cn.master.hub.entity.table.TestPlanModuleTableDef.TEST_PLAN_MODULE;
import static cn.master.hub.entity.table.TestPlanTableDef.TEST_PLAN;

/**
 * @author Created by 11's papa on 2025/9/28
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestPlanManagementService {
    private final SystemProjectMapper projectMapper;

    public TestPlanManagementService(SystemProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    public void checkModuleIsOpen(String resourceId, String resourceType, List<String> moduleMenus) {
        SystemProject project;
        if (Strings.CS.equals(resourceType, TestPlanResourceConfig.CHECK_TYPE_TEST_PLAN)) {
            project = projectMapper.selectOneById(QueryChain.of(TestPlan.class).select(TEST_PLAN.PROJECT_ID)
                    .from(TEST_PLAN).where(TEST_PLAN.ID.eq(resourceId)).oneAs(String.class));
        } else if (Strings.CS.equals(resourceType, TestPlanResourceConfig.CHECK_TYPE_TEST_PLAN_MODULE)) {
            project = projectMapper.selectOneById(QueryChain.of(TestPlanModule.class).select(TEST_PLAN_MODULE.PROJECT_ID)
                    .from(TEST_PLAN_MODULE).where(TEST_PLAN_MODULE.ID.eq(resourceId)).oneAs(String.class));
        } else if (Strings.CS.equals(resourceType, TestPlanResourceConfig.CHECK_TYPE_PROJECT)) {
            project = projectMapper.selectOneById(resourceId);
        } else {
            throw new CustomException(Translator.get("project.module_menu.check.error"));
        }

        if (project == null || project.getModuleSetting().isEmpty()) {
            throw new CustomException(Translator.get("project.module_menu.check.error"));
        }
        List<String> projectModuleMenus = project.getModuleSetting();
        if (!new HashSet<>(projectModuleMenus).containsAll(moduleMenus)) {
            throw new CustomException(Translator.get("project.module_menu.check.error"));
        }
    }
}
