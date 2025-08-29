package cn.master.hub.handler.annotation;

import java.lang.annotation.*;

/**
 * @author Created by 11's papa on 2025/8/29
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoResultHolder {
}
