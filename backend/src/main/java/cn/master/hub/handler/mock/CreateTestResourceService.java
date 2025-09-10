package cn.master.hub.handler.mock;

import cn.master.hub.service.CreateProjectResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Created by 11's papa on 2025/9/10
 */
@Slf4j
@Component
public class CreateTestResourceService implements CreateProjectResourceService {
    @Override
    public void createResources(String projectId) {
        log.info("默认增加当前项目[{}]TEST资源", projectId);
    }
}
