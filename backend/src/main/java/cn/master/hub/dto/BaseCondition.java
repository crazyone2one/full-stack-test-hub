package cn.master.hub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.Strings;

import java.util.List;
import java.util.Map;

/**
 * @author Created by 11's papa on 2025/9/1
 */
@Data
public class BaseCondition {
    @Schema(description = "关键字")
    private String keyword;

    @Schema(description = "过滤字段")
    private Map<String, List<String>> filter;

    public static String transferKeyword(String keyword) {
        if (Strings.CS.contains(keyword, "\\") && !Strings.CS.contains(keyword, "\\\\")) {
            keyword = Strings.CS.replace(keyword, "\\", "\\\\");
        }
        //判断之前有没有转义过。转义过就不再转义。耍花活的自己想办法解决
        if (Strings.CS.contains(keyword, "%") && !Strings.CS.contains(keyword, "\\%")) {
            keyword = Strings.CS.replace(keyword, "%", "\\%");
        }
        if (Strings.CS.contains(keyword, "_") && !Strings.CS.contains(keyword, "\\_")) {
            keyword = Strings.CS.replace(keyword, "_", "\\_");
        }
        return keyword;
    }

    public void initKeyword(String keyword) {
        //  直接初始化keyword
        this.keyword = keyword;
    }
}
