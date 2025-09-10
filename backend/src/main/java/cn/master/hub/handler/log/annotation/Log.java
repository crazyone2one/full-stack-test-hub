package cn.master.hub.handler.log.annotation;

import cn.master.hub.handler.log.OperationLogType;

import java.lang.annotation.*;

/**
 * @author Created by 11's papa on 2025/9/10
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**
     * 操作类型
     */
    OperationLogType type() default OperationLogType.SELECT;

    /***
     * 操作函数
     */
    String expression();

    /**
     * 传入执行类
     *
     */
    Class[] msClass() default {};
}
