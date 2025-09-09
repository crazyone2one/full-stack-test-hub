package cn.master.hub.handler.invoker;

import cn.master.hub.service.CreateProjectResourceService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/1
 */
@Component
public class ProjectServiceInvoker {
    private final List<CreateProjectResourceService> createProjectResourceServices;

    public ProjectServiceInvoker(List<CreateProjectResourceService> createProjectResourceServices) {
        this.createProjectResourceServices = createProjectResourceServices;
    }

    public void invokeCreateServices(String projectId) {
        for (CreateProjectResourceService service : createProjectResourceServices) {
            service.createResources(projectId);
        }
    }
}
