package cn.master.hub.service;

import cn.master.hub.constants.ProjectApplicationType;
import cn.master.hub.entity.ProjectApplication;
import cn.master.hub.entity.ProjectVersion;
import cn.master.hub.mapper.ProjectVersionMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Created by 11's papa on 2025/9/1
 */
@Slf4j
@Component
public class CreateVersionResourceService implements CreateProjectResourceService {
    public static final String DEFAULT_VERSION = "v1.0";
    public static final String DEFAULT_VERSION_STATUS = "open";
    @Resource
    private ProjectVersionMapper projectVersionMapper;

    @Override
    public void createResources(String projectId) {
        // 初始化版本V1.0, 初始化版本配置项
        ProjectVersion defaultVersion = new ProjectVersion();
        defaultVersion.setProjectId(projectId);
        defaultVersion.setName(DEFAULT_VERSION);
        defaultVersion.setStatus(DEFAULT_VERSION_STATUS);
        defaultVersion.setLatest(true);
        defaultVersion.setCreateUser("admin");
        projectVersionMapper.insert(defaultVersion);

        ProjectApplication projectApplication = new ProjectApplication();
        projectApplication.setProjectId(projectId);
        projectApplication.setType(ProjectApplicationType.VERSION.VERSION_ENABLE.name());
        projectApplication.setTypeValue("FALSE");
        log.info("初始化当前项目[{}]相关版本资源", projectId);
    }
}
