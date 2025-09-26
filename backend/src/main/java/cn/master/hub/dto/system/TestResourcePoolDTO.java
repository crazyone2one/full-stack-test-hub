package cn.master.hub.dto.system;

import cn.master.hub.entity.TestResourcePool;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/25
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TestResourcePoolDTO extends TestResourcePool {
    private TestResourceDTO testResourceDTO;

    @Schema(description =  "资源池是否在使用中")
    private Boolean inUsed;

    @Schema(description =  "最大并发数")
    private int maxConcurrentNumber;

    @Schema(description =  "剩余并发数")
    private int lastConcurrentNumber;;

    @Schema(description =  "组织名称集合")
    private List<String> orgNames;

}