package cn.master.hub.dto.response;

import lombok.Data;

/**
 * @author Created by 11's papa on 2025/9/1
 */
@Data
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String lastOrganizationId;
    private String lastProjectId;
    private String avatar;
}
