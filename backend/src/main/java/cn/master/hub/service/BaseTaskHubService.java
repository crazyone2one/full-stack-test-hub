package cn.master.hub.service;

import cn.master.hub.dto.request.BasePageRequest;
import cn.master.hub.dto.system.TaskHubScheduleDTO;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/25
 */
public interface BaseTaskHubService {
    Page<TaskHubScheduleDTO> getSchedulePage(BasePageRequest request, List<String> projectIds);
}
