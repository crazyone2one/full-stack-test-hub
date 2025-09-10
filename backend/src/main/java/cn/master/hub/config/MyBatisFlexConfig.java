package cn.master.hub.config;

import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.keygen.KeyGenerators;
import com.mybatisflex.core.query.QueryColumnBehavior;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

/**
 * @author Created by 11's papa on 2025/8/29
 */
@Configuration
public class MyBatisFlexConfig implements MyBatisFlexCustomizer {
    @Override
    public void customize(FlexGlobalConfig flexGlobalConfig) {
        flexGlobalConfig.setLogicDeleteColumn("deleted");
        FlexGlobalConfig.KeyConfig keyConfig = new FlexGlobalConfig.KeyConfig();
        keyConfig.setKeyType(KeyType.Generator);
        keyConfig.setValue(KeyGenerators.flexId);
        FlexGlobalConfig.getDefaultConfig().setKeyConfig(keyConfig);
        flexGlobalConfig.setDbType(DbType.MYSQL);

        // 使用内置规则自动忽略 null 和 空字符串
        QueryColumnBehavior.setIgnoreFunction(QueryColumnBehavior.IGNORE_EMPTY);
        // 使用内置规则自动忽略 null 和 空白字符串
        QueryColumnBehavior.setIgnoreFunction(QueryColumnBehavior.IGNORE_BLANK);
        // 如果传入的值是集合或数组，则使用 in 逻辑，否则使用 =（等于） 逻辑
        QueryColumnBehavior.setSmartConvertInToEquals(true);
        // 使用自定义规则忽略空集合参数
        // 当参数是空列表或数组时，不拼接查询条件
        QueryColumnBehavior.setIgnoreFunction((object) -> {
            if (object == null) {
                return true;
            }
            if (object instanceof Collection) {
                return ((Collection<?>) object).isEmpty();
            }
            if (object.getClass().isArray()) {
                return java.lang.reflect.Array.getLength(object) == 0;
            }
            return false;
        });
    }
}
